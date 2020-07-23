package com.wen.commons.utils.excel;

import com.wen.commons.exception.ExcelException;
import com.wen.commons.utils.BeanUtils;
import com.wen.commons.utils.MobileUtils;
import com.wen.commons.utils.Pair;
import com.wen.commons.utils.StringUtils;
import com.wen.commons.utils.excel.exp.*;
import com.wen.commons.utils.excel.imp.ImportColumn;
import com.wen.commons.utils.excel.imp.ImportError;
import com.wen.commons.utils.excel.imp.ImportExcel;
import com.wen.commons.utils.excel.imp.ImportExcelChecker;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 导出通用Excel工具类
 *
 * @author denis.huang
 * @date 2016年4月27日
 */
public abstract class ExcelUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    private static Map<String, ImportExcelChecker> excelChecker = new HashMap<>();

    /**
     * 导出excel通用方法
     *
     * @param list          数据列表
     * @param clazz
     * @param headerConvert 表头转换
     * @param sheetName     指定表格名
     * @param is            文件流，和sheetName配合导出多个sheet
     * @return
     * @throws IOException
     */
    public static <E> ByteArrayOutputStream exportExcel(List<E> list, Class<? extends BaseEO<E>> clazz,
                                                        Map<String, String> headerConvert, String sheetName, InputStream is) throws IOException {
        Workbook wb = getWorkbook(clazz, is);
        Sheet sheet = initSheet(wb, clazz, sheetName);

        int rowNum = createTableHead(wb, sheet, clazz, headerConvert);
        createTableBody(wb, sheet, clazz, list, rowNum);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        wb.write(output);

        return output;
    }

    /**
     * 获取03或07格式的workbook
     *
     * @throws IOException
     */
    private static Workbook getWorkbook(Class<?> clazz, InputStream is) throws IOException {
        ExcelFormat excelFormat = clazz.getAnnotation(ExcelFormat.class);
        Workbook wb;
        if (excelFormat == null || excelFormat.value().equals("xls")) {
            if (is != null) {
                wb = new HSSFWorkbook(is);
            } else {
                wb = new HSSFWorkbook();
            }
        } else {
            if (is != null) {
                wb = new XSSFWorkbook(is);
            } else {
                wb = new XSSFWorkbook();
            }
        }
        return wb;
    }

    /**
     * 初始化sheet, 设置宽度
     */
    private static Sheet initSheet(Workbook wb, Class<?> clazz, String sheetName) {
        Sheet sheet;
        if (StringUtils.isEmpty(sheetName)) {
            sheet = wb.createSheet("sheet1");
        } else {
            sheet = wb.createSheet(sheetName);
        }

        Field[] fields = BeanUtils.findSuperFields(clazz);

        if (fields.length == 0) {
            logger.error("对象没有可导出字段");
            throw new ExcelException();
        }

        int colCount = 0;
        for (int n = 0; n < fields.length; n++) {
            ExcelColumn tc = fields[n].getAnnotation(ExcelColumn.class);
            if (tc != null) {
                sheet.setColumnWidth(n, tc.width() * 256);
                colCount++;
            }
        }

        if (colCount == 0) {
            logger.error("对象没有可导出字段");
            throw new ExcelException();
        }

        return sheet;
    }

    /**
     * 生成表主体
     *
     * @param <E>
     */
    private static <E> void createTableBody(Workbook wb, Sheet sheet, Class<?> clazz, List<E> list, int rowNum) {
        if (list == null || list.size() == 0) {
            return;
        }

        short rowHeight = getRowHeight(clazz);
        Field[] fields = BeanUtils.findSuperFields(clazz);

        Map<String, ExcelColumn> tableColumnMap = getTableColumnMap(fields);
        Map<String, CellStyle> cellStyleMap = getCellStyleMap(wb, tableColumnMap);

        Map<Integer, Integer> rowspanMap = new HashMap<>();
        for (int n = 0; n < list.size(); n++) {
            if (list.get(n) == null) {
                continue;
            }

            Row row = sheet.createRow(n + rowNum);
            row.setHeight((short) (rowHeight * 30));

            int cellNum = 0;
            for (Field field : fields) {
                ExcelColumn tc = tableColumnMap.get(field.getName());
                if (tc != null) {
                    CellStyle style = cellStyleMap.get(field.getName());
                    Cell cell = row.createCell(cellNum++);
                    cell.setCellStyle(style);

                    try {
                        if (field.getType().equals(Date.class)) {
                            Date date = (Date) field.get(list.get(n));
                            if (date != null) {
                                cell.setCellValue(getDateValue(tc.format(), date));
                            }
                        } else if (field.getType().equals(Integer.class)) {
                            Integer value = (Integer) field.get(list.get(n));
                            if (value != null) {
                                cell.setCellValue(value);
                            }
                        } else if (field.getType().equals(Double.class)) {
                            Double value = (Double) field.get(list.get(n));
                            if (value != null) {
                                cell.setCellValue(value);
                            }
                        } else {
                            String value = ConvertUtils.convert(field.get(list.get(n)));

                            Integer rowspan = rowspanMap.get(cellNum - 1);
                            if (StringUtils.isNotEmpty(value) && value.equals("#row")) {
                                if (rowspan == null) {
                                    rowspanMap.put(cellNum - 1, 1);
                                } else {
                                    rowspanMap.put(cellNum - 1, rowspan + 1);
                                }
                            } else {
                                if (rowspan != null && rowspan != 0) {
                                    CellRangeAddress region = new CellRangeAddress(n + rowNum - rowspan - 1,
                                            n + rowNum - 1, cellNum - 1, cellNum - 1);
                                    sheet.addMergedRegion(region);
                                    rowspanMap.put(cellNum - 1, 0);
                                }

                                cell.setCellValue(value);
                            }
                        }
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        logger.error("设置值异常,field=" + field.getName(), e);
                        cell.setCellValue("");
                    }
                }
            }
        }

        // 为最后还没合并的列合并
        for (Integer celln : rowspanMap.keySet()) {
            if (rowspanMap.get(celln) != null && rowspanMap.get(celln) != 0) {
                CellRangeAddress region = new CellRangeAddress(list.size() + rowNum - 1 - rowspanMap.get(celln),
                        list.size() + rowNum - 1, celln, celln);
                sheet.addMergedRegion(region);
            }
        }
    }

    /**
     * 将每列的样式放进map中
     *
     * @param wb
     * @param tableColumnMap
     * @return
     */
    private static Map<String, CellStyle> getCellStyleMap(Workbook wb, Map<String, ExcelColumn> tableColumnMap) {
        Map<String, CellStyle> cellStyleMap = new HashMap<>();
        for (Map.Entry<String, ExcelColumn> entry : tableColumnMap.entrySet()) {
            cellStyleMap.put(entry.getKey(), createCellStyle(wb, entry.getValue()));
        }
        return cellStyleMap;
    }

    /**
     * 将TableColumn注解放在map中加快读取速度
     */
    private static Map<String, ExcelColumn> getTableColumnMap(Field[] fields) {
        Map<String, ExcelColumn> tableColumnMap = new HashMap<>();
        for (Field field : fields) {
            tableColumnMap.put(field.getName(), field.getAnnotation(ExcelColumn.class));
        }
        return tableColumnMap;
    }

    /**
     * 取行高
     */
    private static <E> short getRowHeight(Class<?> clazz) {
        ExcelRowHeight rh = clazz.getAnnotation(ExcelRowHeight.class);
        if (rh == null) {
            return 12;
        }
        return rh.value();
    }

    /**
     * 格式化日期类型
     */
    private static String getDateValue(String format, Date date) {
        if (date == null) {
            return "";
        }

        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 表主体样式
     */
    private static CellStyle createCellStyle(Workbook wb, ExcelColumn tc) {
        CellStyle style = wb.createCellStyle();
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
        style.setAlignment(tc.alignment()); // 水平对齐方式
        style.setFillForegroundColor(tc.color()); // 背景色
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 背景色填充方式
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN); // 左边框
        style.setBorderTop(HSSFCellStyle.BORDER_THIN); // 上边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN); // 右边框

        Font font = wb.createFont();
        font.setFontHeightInPoints(tc.fontSize()); // 设置字体大小
        font.setBoldweight(tc.bold()); // 设置字体加粗
        font.setFontName(tc.fontName()); // 设置字体
        font.setUnderline(tc.underline()); // 设置下划线样式
        font.setColor(tc.fontColor()); // 设置字体颜色
        style.setFont(font);

        return style;
    }

    /**
     * 生成表头
     *
     * @return 生成表头后行号
     */
    private static int createTableHead(Workbook wb, Sheet sheet, Class<?> clazz, Map<String, String> headerConvert) {
        ExcelHeader[] thArray = getTableHeader(clazz);

        int rowNum = 0;
        for (ExcelHeader th : thArray) {
            CellStyle headStyle = createHeadStyle(wb, th);
            Row row = sheet.createRow(rowNum);
            row.setHeight((short) (th.height() * 30)); // 设置行高

            String[] headers = th.value().split(",");
            if (headers.length == 0) {
                logger.error("表头不能为空");
                throw new ExcelException();
            }

            int cellNum = headers.length - 1;
            int colspan = 0;
            for (int n = headers.length - 1; n >= 0; n--) {// 从最后一列开始处理，为了能方便处理#col
                Cell cell = row.createCell(cellNum);
                cell.setCellStyle(headStyle);

                switch (headers[n]) {
                    case "#col": // 有可能会合并多个单元格，所以累计起来
                        colspan++;
                        break;
                    case "#row":
                        CellRangeAddress region = new CellRangeAddress(rowNum - 1, rowNum, cellNum, cellNum);
                        sheet.addMergedRegion(region);
                        break;
                    default:
                        if (colspan != 0) {
                            region = new CellRangeAddress(rowNum, rowNum, cellNum, cellNum + colspan);
                            sheet.addMergedRegion(region);
                            colspan = 0;
                        }

                        cell.setCellValue(convertHeard(headerConvert, headers[n]));
                        break;
                }

                cellNum--;
            }

            rowNum++;
        }

        return rowNum;
    }

    /**
     * 表头替换动态内容
     *
     * @param headerConvert
     * @param value
     * @return
     */
    private static String convertHeard(Map<String, String> headerConvert, String value) {
        if (headerConvert != null) {
            for (Map.Entry<String, String> entry : headerConvert.entrySet()) {
                if (StringUtils.isEmpty(entry.getValue())) {
                    value = value.replace(entry.getKey(), "");
                } else {
                    value = value.replace(entry.getKey(), entry.getValue());
                }
            }
        }

        return value;
    }

    /**
     * 获取表头注解
     */
    private static ExcelHeader[] getTableHeader(Class<?> clazz) {
        ExcelHeaders ths = clazz.getAnnotation(ExcelHeaders.class);
        ExcelHeader[] thArray = null;

        if (ths != null) {
            thArray = ths.value();
        } else {
            ExcelHeader th = clazz.getAnnotation(ExcelHeader.class);
            if (th != null) {
                thArray = new ExcelHeader[1];
                thArray[0] = th;
            }
        }

        if (thArray == null || thArray.length == 0) {
            logger.error("没有定义表头");
            throw new ExcelException();
        }

        return thArray;
    }

    /**
     * 创建表头样式
     */
    private static CellStyle createHeadStyle(Workbook wb, ExcelHeader th) {
        CellStyle style = wb.createCellStyle();
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直对齐方式
        style.setAlignment(th.alignment()); // 水平对齐方式
        style.setFillForegroundColor(th.color()); // 设置背景色
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 背景色填充方式
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN); // 左边框
        style.setBorderTop(HSSFCellStyle.BORDER_THIN); // 上边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN); // 右边框

        Font font = wb.createFont();
        font.setFontHeightInPoints(th.fontSize()); // 设置字体大小
        font.setBoldweight(th.boldWeight()); // 设置粗体
        font.setFontName(th.fontName()); // 设置字体
        style.setFont(font);

        return style;
    }

    /**
     * 导入excel通用方法
     *
     * @param is    文件流
     * @param clazz 目标类型
     * @param isXls 是否03格式
     * @return Pair<目标对象集合, Map<异常序号, 异常消息集合>>
     * @throws ExcelException
     */
    public static <E> Pair<List<E>, Map<Integer, List<ImportError>>> importExcel(InputStream is,
        Class<? extends BaseEO<E>> clazz, boolean isXls) throws ExcelException {
        Sheet sheet;
        try {
            sheet = getSheet(is, isXls);
        } catch (IOException e) {
            logger.error("文件不是excel格式", e);
            throw new ExcelException();
        }

        return transformEO(clazz, sheet);
    }

    /**
     * excel表格转换成对象集合
     *
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     */
    private static <E> Pair<List<E>, Map<Integer, List<ImportError>>> transformEO(Class<? extends BaseEO<E>> clazz,
        Sheet sheet) throws ExcelException {
        ImportExcel ie = clazz.getAnnotation(ImportExcel.class);
        Field[] fields = BeanUtils.findSuperFields(clazz);
        Map<Integer, Field> fieldMap = getFieldMap(fields);

        int notEmptyNum = getTotalNotEmpty(fields);// 对象非空值数量

        Map<Integer, List<ImportError>> errorMsg = new HashMap<>();
        List<E> list = new ArrayList<>();
        int n = 0;
        for (int i = ie.start(); i <= sheet.getLastRowNum(); i++) {
            E eo = getInstance(clazz);

            Row row = sheet.getRow(i);

            List<ImportError> rowError = new ArrayList<>();// 第一行的错误消息集合
            boolean flag = false;// 标识是数据还是空行

            int tableEmptyNum = 0;// 表格中非空字段但是空值的数量

            for (int j = 0; row != null && j < row.getLastCellNum(); j++) {
                flag = true;

                Cell cell = row.getCell(j);
                Field field = fieldMap.get(j);
                if (field != null) {
                    ImportColumn ic = field.getAnnotation(ImportColumn.class);

                    if (cell == null) {
                        if (ic != null && !ic.empty()) {
                            tableEmptyNum++;
                            rowError.add(ImportError.makeError(i + 1, j + 1, "为空"));
                        }

                        // 所有非空字段都空，标识不是数据
                        if (tableEmptyNum == notEmptyNum) {
                            flag = false;
                            break;
                        }

                        continue;
                    }

                    try {
                        String msg;// 异常消息

                        // 空值处理
                        if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
                            if (ic != null && !ic.empty()) {
                                tableEmptyNum++;
                                rowError.add(ImportError.makeError(i + 1, j + 1, "为空"));
                            }

                            // 所有非空字段都空，标识不是数据
                            if (tableEmptyNum == notEmptyNum) {
                                flag = false;
                                break;
                            }

                            continue;
                        }
                        if (field.getType().equals(Integer.class)) {
                            if (cell.getCellType() != Cell.CELL_TYPE_NUMERIC) {
                                msg = "不是数字类型";
                            } else {
                                try {
                                    Double value = cell.getNumericCellValue();
                                    msg = checkValue(value, ic);

                                    field.set(eo, value.intValue());
                                } catch (IllegalStateException e) {
                                    msg = "不是数字类型";
                                }
                            }
                        } else if (field.getType().equals(Double.class)) {
                            if (cell.getCellType() != Cell.CELL_TYPE_NUMERIC) {
                                msg = "不是数字类型";
                            } else {
                                try {
                                    Double value = cell.getNumericCellValue();
                                    msg = checkValue(value, ic);

                                    field.set(eo, value);
                                } catch (IllegalStateException e) {
                                    msg = "不是数字类型";
                                }
                            }
                        } else if (field.getType().equals(Date.class)) {
                            try {
                                Date value = cell.getDateCellValue();
                                msg = checkValue(value, ic);

                                field.set(eo, value);
                            } catch (IllegalStateException e) {
                                msg = "不是日期格式";
                            }
                        } else {
                            try {
                                String value = cell.getStringCellValue();
                                msg = checkValue(value, ic);

                                field.set(eo, value);
                            } catch (IllegalStateException e) {
                                msg = "数据类型不匹配";
                            }
                        }

                        if (StringUtils.isNotEmpty(msg)) {
                            rowError.add(ImportError.makeError(i + 1, j + 1, msg));
                        }
                    } catch (IllegalAccessException e) {
                        logger.error(field.getClass().getName() + "." + field.getName() + ",无法设置值");
                        throw new ExcelException();
                    }
                }
            }

            if (flag) {
                list.add(eo);

                // 只有主键不空的才加进错误消息集合
                errorMsg.put(n++, rowError);

                if (!rowError.isEmpty()) {
                    logger.warn(StringUtils.join(rowError, ","));
                }
            }
        }
        return Pair.makePair(list, errorMsg);
    }

    /**
     * 获取非空值数量
     *
     * @param fields
     * @return
     */
    private static int getTotalNotEmpty(Field[] fields) {
        int notEmptyNum = 0;
        for (Field field : fields) {
            ImportColumn ic = field.getAnnotation(ImportColumn.class);

            if (ic != null && !ic.empty()) {
                notEmptyNum++;
            }
        }
        return notEmptyNum;
    }

    /**
     * 校验数据合法性
     *
     * @param value
     * @param ic
     */
    private static String checkValue(Object value, ImportColumn ic) {
        if (ic != null && ic.checkMobile()) {
            if (!MobileUtils.checkMobile((String) value)) {
                return "不是合法手机号";
            }
        }

        if (ic != null && ic.size() != 0) {
            if (((String) value).length() > ic.size()) {
                return "长度不能大于" + ic.size();
            }
        }

        // 自定义校验
        List<ImportExcelChecker> checkerList = getChecker(ic);
        for (ImportExcelChecker checker : checkerList) {
            String msg = checker.check(value);
            if (StringUtils.isNotEmpty(msg)) {
                return msg;
            }
        }

        return null;
    }

    /**
     * 获取自定义校验方法
     *
     * @param ic
     * @return
     */
    private static List<ImportExcelChecker> getChecker(ImportColumn ic) {
        List<ImportExcelChecker> checkerList = new ArrayList<>();

        if (ic.checker().length > 0 && !ic.checker()[0].equals(ImportExcelChecker.class)) {
            for (Class<? extends ImportExcelChecker> checker : ic.checker()) {
                ImportExcelChecker check = excelChecker.computeIfAbsent(checker.getName(), k -> getInstance(checker));
                checkerList.add(check);
            }
        }
        return checkerList;
    }

    /**
     * 实例化eo
     *
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <E> E getInstance(Class<?> clazz) {
        E eo;
        try {
            eo = (E) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(clazz.getName() + ",无法实例化", e);
            throw new ExcelException();
        }
        return eo;
    }

    /**
     * 流转换成Sheet对象
     *
     * @throws IOException
     */
    private static Sheet getSheet(InputStream is, boolean isXls) throws IOException {
        Workbook wk;

        if (isXls) {
            wk = new HSSFWorkbook(is);
        } else {
            wk = new XSSFWorkbook(is);
        }

        return wk.getSheetAt(0);
    }

    /**
     * 字段对应列数与字段的Map
     */
    private static Map<Integer, Field> getFieldMap(Field[] fields) {
        Map<Integer, Field> fieldMap = new HashMap<>();
        for (Field field : fields) {
            ImportColumn ic = field.getAnnotation(ImportColumn.class);
            if (ic != null) {
                fieldMap.put(ic.col(), field);
            }
        }
        return fieldMap;
    }
}

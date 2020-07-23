package com.wen.commons.spring;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;

import com.wen.commons.utils.DateUtils;
import com.wen.commons.utils.StringUtils;

/**
 * 时间类型数据转换器
 */
public class MultiDateFormatEditor extends PropertyEditorSupport {
    @Override
    public String getAsText() {
        Date value = (Date) getValue();
        return value == null ? "" : DateUtils.DEFAULT_DATETIME_FORMATER.get().format(value);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isEmpty(text)) {
            setValue(null);
        } else {
            Date dt;
            try {
                if (text.length() == 19) {
                    dt = DateUtils.DEFAULT_DATETIME_FORMATER.get().parse(text);
                } else if (text.length() == 14) {
                    dt = DateUtils.YYYYMMDDHHMMSS.get().parse(text);
                } else {
                    dt = DateUtils.DEFAULT_DATE_FORMATER.get().parse(text);
                }
            } catch (ParseException ex) {
                throw new IllegalArgumentException(text);
            }
            setValue(dt);
        }
    }
}

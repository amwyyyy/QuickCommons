package com.wen.commons.utils.excel.exp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表头，字段用","分割，#row代表垂直合并占位，#col代表水平合并占位
 * 可单独使用，多级表头配合ExcelHeaders使用
 * @author denis.huang
 * @date 2016年4月26日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelHeader
{
	String value();
	
	/** 行高 */
	short height() default 10;
	
	/** 背景色，默认白色 */
	short color() default 0x9;
	
	/** 对齐方式，默认居中 */
	short alignment() default 0x2;
	
	/** 字体名 */
	String fontName() default "宋体";//TODO linux下可能会有问题，待修改
	
	/** 字体大小 */
	short fontSize() default 15;
	
	/** 字体加粗，默认不加粗 */
	short boldWeight() default 0x190;
}

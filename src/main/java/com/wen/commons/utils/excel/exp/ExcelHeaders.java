package com.wen.commons.utils.excel.exp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多级表头
 * @author denis.huang
 * @date 2016年4月26日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelHeaders
{
	ExcelHeader[] value();
}

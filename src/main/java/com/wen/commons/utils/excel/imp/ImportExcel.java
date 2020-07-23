package com.wen.commons.utils.excel.imp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 导入excel公共信息
 * 
 * @author denis.huang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportExcel {
	/** 从第几行开始读数据 */
	int start() default 0;
}

package com.wen.commons.utils.excel.imp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 导入excel列信息
 * 
 * @author denis.huang
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportColumn {
	/** 第几列数据 */
	int col() default 0;

	/** 字段能否为空 */
	boolean empty() default false;

	/** 字符串最大长度 */
	int size() default 0;

	/** 检查手机号合法性 */
	boolean checkMobile() default false;

	/** 自定义校验，如手机号唯一 */
	Class<? extends ImportExcelChecker>[] checker() default ImportExcelChecker.class;

	// TODO 更多校验逻辑
}

package com.wen.commons.exception;

/**
 * 错误码定义
 * 
 * @author denis.huang
 * @since 2016年12月17日
 */
public class ErrorCodeConst {
	public static final ErrorCode ERRCODE_OK = new ErrorCode(0, "成功");

	public static final ErrorCode ERRCODE_NOLOGIN = new ErrorCode(10, "没有登录");

	public static final ErrorCode ERRCODE_NOPERM = new ErrorCode(20, "没有权限");

	public static final ErrorCode ERRCODE_LOGIN_ERROR = new ErrorCode(30, "登录失败");

	public static final ErrorCode ERRCODE_NUMBER_FORMAT_ERROR = new ErrorCode(40, "参数类型错误");

	public static final ErrorCode EXCEL_FORMAT_ERROR = new ErrorCode(50, "excel文件格式错误");

	public static final ErrorCode ERRCODE_PARAM = new ErrorCode(60, "参数不合法");

	public static final ErrorCode ERRCODE_OTHER = new ErrorCode(9999, "服务异常");
}

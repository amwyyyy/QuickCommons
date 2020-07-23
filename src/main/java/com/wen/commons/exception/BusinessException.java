package com.wen.commons.exception;

/**
 * 业务异常类
 *
 * @author denis.huang
 * @since 2017年2月15日
 */
public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = -7887057238055620806L;
	private final ErrorCode errorCode;

	public BusinessException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public BusinessException(ErrorCode errorCode, String msg) {
		super(msg);
		this.errorCode = errorCode;
	}

	public BusinessException(ErrorCode errorCode, Throwable ex) {
		super(ex);
		this.errorCode = errorCode;
	}

	public BusinessException(ErrorCode errorCode, String msg, Throwable ex) {
		super(msg, ex);
		this.errorCode = errorCode;
	}

	public ErrorCode getCode() {
		return errorCode;
	}

	@Override
	public String getMessage() {
		return errorCode == null ? null : errorCode.toString();
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		// 保留堆栈
		return super.fillInStackTrace();
	}
}

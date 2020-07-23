package com.wen.commons.exception;

import java.io.Serializable;

/**
 * 业务异常错误码定义类
 * 
 * @author denis.huang
 * @since 2017年2月15日
 */
public class ErrorCode implements Serializable {
	private static final long serialVersionUID = 1982607259137204522L;

	private final int code;
	private String message;

	public ErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof ErrorCode)) {
			return false;
		}

		ErrorCode other = (ErrorCode) obj;
		return this.code == other.getCode();
	}

	@Override
	public int hashCode() {
		final int i = 7;
		return 31 * i + this.code;
	}

	@Override
	public String toString() {
		return "ErrorCode [code=" + code + ", message=" + message + "]";
	}
}

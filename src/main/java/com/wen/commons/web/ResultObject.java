package com.wen.commons.web;

import java.util.HashMap;

import com.wen.commons.exception.ErrorCode;
import com.wen.commons.exception.ErrorCodeConst;

/**
 * 返回前端的包装对象
 * 
 * @author denis.huang
 * @since 2017年2月15日
 */
public class ResultObject extends HashMap<String, Object> {
	private static final long serialVersionUID = 385654030713612995L;

	/** 返回错误码字段 */
	private static final String PARAM_ERRCODE = "errCode";

	/** 返回消息字段 */
	private static final String PARAM_MSG = "msg";

	/** 返回数据字段 */
	private static final String PARAM_DATA = "data";

	public ResultObject() {
		setErrCode(ErrorCodeConst.ERRCODE_OK);
	}

	public Integer getErrCode() {
		return (Integer) this.get(PARAM_ERRCODE);
	}

	public void setErrCode(ErrorCode errCode) {
		this.put(PARAM_ERRCODE, errCode.getCode());
		this.put(PARAM_MSG, errCode.getMessage());
	}

	public String getMsg() {
		return (String) this.get(PARAM_MSG);
	}

	public void setMsg(String msg) {
		this.put(PARAM_MSG, msg);
	}

	public Object getData() {
		return this.get(PARAM_DATA);
	}

	public void setData(Object data) {
		this.put(PARAM_DATA, data);
	}

	public boolean isOk() {
		return this.getErrCode().equals(ErrorCodeConst.ERRCODE_OK.getCode());
	}
}

package com.wen.commons.utils.excel.imp;

/**
 * 导入excel错误消息体
 * 
 * @author denis.huang
 */
public class ImportError {
	private Integer row;// 行
	private Integer col;// 列
	private String msg;// 错误消息

	public static ImportError makeError(int row, int col, String msg) {
		ImportError error = new ImportError();
		error.setCol(col);
		error.setRow(row);
		error.setMsg(msg);
		return error;
	}

	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}

	public Integer getCol() {
		return col;
	}

	public void setCol(Integer col) {
		this.col = col;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}

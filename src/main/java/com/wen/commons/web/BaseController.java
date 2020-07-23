package com.wen.commons.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pagehelper.Page;
import com.wen.commons.exception.ErrorCode;
import com.wen.commons.exception.ErrorCodeConst;

/**
 * 基础controller类，controller都要继承此类
 * 
 * @author huangwg
 * @since 2016-10-10
 */
public abstract class BaseController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String PARAM_PAGE_NUM = "pageNum";
    public static final String PARAM_PAGE_SIZE = "pageSize";
    public static final String PARAM_SORT_NAME = "sortName";
    public static final String PARAM_SORT_TYPE = "sortType";

    /**
     * 创建成功返回对象
     * 
     * @param data
     * @return
     */
    protected ResultObject buildSuccessResp(Object data) {
        ResultObject resp = new ResultObject();
        resp.setData(data);
        return resp;
    }

    /**
     * 创建成功返回对象
     * 
     * @return
     */
    protected ResultObject buildSuccessResp() {
        ResultObject resp = new ResultObject();
        resp.setData(null);
        return resp;
    }

    /**
     * 创建失败返回对象
     * 
     * @param msg
     * @return
     */
    protected ResultObject buildFailResp(String msg) {
        ResultObject resp = new ResultObject();
        resp.setErrCode(ErrorCodeConst.ERRCODE_OTHER);
        resp.setMsg(msg);
        return resp;
    }

    /**
     * 创建失败返回对象
     * 
     * @param errCode
     * @return
     */
    protected ResultObject buildFailResp(ErrorCode errCode) {
        ResultObject resp = new ResultObject();
        resp.setErrCode(errCode);
        return resp;
    }

    /**
     * 分页结果返回
     * 
     * @param pageList
     * @return
     */
    protected <E> ResultObject toPageResp(Page<E> pageList) {
        ResultObject resp = new ResultObject();
        resp.put("pageSize", pageList.getPageSize());
        resp.put("pageTotal", pageList.getTotal());
        resp.setData(pageList.getResult());
        return resp;
    }
}

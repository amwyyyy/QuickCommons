package com.wen.commons.spring;

import java.util.Date;
import java.util.Iterator;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.wen.commons.exception.ErrorCodeConst;
import com.wen.commons.exception.ExcelException;
import com.wen.commons.web.ResultObject;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Controller增强切面
 * 
 * @author denis.huang
 * @since 2017年2月15日
 */
@ControllerAdvice
public class BaseControllerAdvice {
    private static final Logger logger = LoggerFactory.getLogger(HandlerExceptionResolver.class);

    /**
     * 注册类型转换器
     *
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // 注册日期转换器, 可以兼容yyyy-MM-dd和yyyy-MM-dd HH:mm:ss两种格式
        binder.registerCustomEditor(Date.class, new MultiDateFormatEditor());

        // 对所有的文本字符串进行trim操作, 去掉空后空格
        StringTrimmerEditor trimmerEditor = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, trimmerEditor);
    }

    /**
     * 统一异常处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    @ResponseBody
    public ResultObject resolveException(Exception ex) {
        ResultObject resp = new ResultObject();

        if (ex instanceof UnknownAccountException) {
            resp.setErrCode(ErrorCodeConst.ERRCODE_NOLOGIN);
            resp.setMsg("账号不存在");
        } else if (ex instanceof IncorrectCredentialsException) {
            resp.setErrCode(ErrorCodeConst.ERRCODE_NOLOGIN);
            resp.setMsg("密码错误");
        } else if (ex instanceof UnauthenticatedException) {
            resp.setErrCode(ErrorCodeConst.ERRCODE_NOLOGIN);
            resp.setMsg("没有登录");
        } else if (ex instanceof BindException) {
            resp.setErrCode(ErrorCodeConst.ERRCODE_PARAM);
        } else if (ex instanceof UnauthorizedException) {
            resp.setErrCode(ErrorCodeConst.ERRCODE_NOPERM);
        } else if (ex instanceof AuthenticationException) {
            resp.setErrCode(ErrorCodeConst.ERRCODE_LOGIN_ERROR);
        } else if (ex instanceof ExcelException) {
            resp.setErrCode(ErrorCodeConst.EXCEL_FORMAT_ERROR);
        } else if (ex instanceof NumberFormatException) {
            resp.setErrCode(ErrorCodeConst.ERRCODE_NUMBER_FORMAT_ERROR);
        } else if (ex instanceof ConstraintViolationException) {
            resp.setErrCode(ErrorCodeConst.ERRCODE_PARAM);

            Iterator<ConstraintViolation<?>> it = ((ConstraintViolationException) ex).getConstraintViolations().iterator();
            if (it.hasNext()) {
                ConstraintViolationImpl violation = (ConstraintViolationImpl) it.next();

                PathImpl path = (PathImpl) violation.getPropertyPath();
                resp.setMsg(path.getLeafNode().getName() + violation.getMessage());
            }
        } else {
            resp.setErrCode(ErrorCodeConst.ERRCODE_OTHER);
            resp.setMsg(ex.getMessage());
        }

        logger.warn("错误码：" + resp.getErrCode(), ex);

        return resp;
    }
}

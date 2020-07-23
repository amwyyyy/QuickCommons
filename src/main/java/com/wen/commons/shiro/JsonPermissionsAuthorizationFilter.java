package com.wen.commons.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.wen.commons.utils.JsonUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wen.commons.exception.ErrorCode;
import com.wen.commons.exception.ErrorCodeConst;
import com.wen.commons.web.ResultObject;

/**
 * 继承perms过滤器，不通过时返回json而不是跳转
 *
 * @author denis.huang
 * @since 2017年2月15日
 */
public class JsonPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter
{
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException
    {
        Subject subject = getSubject(request, response);
        if (subject.getPrincipal() == null)
        {
            saveRequest(request);

            JsonUtils.writeToJson(response, ErrorCodeConst.ERRCODE_NOLOGIN);
        }
        else
        {
            JsonUtils.writeToJson(response, ErrorCodeConst.ERRCODE_NOPERM);
        }
        return false;
    }
}

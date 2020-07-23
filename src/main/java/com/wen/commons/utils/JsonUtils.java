package com.wen.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wen.commons.exception.ErrorCode;
import com.wen.commons.web.ResultObject;

import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author denis.huang
 * @since 2017/2/24
 */
public abstract class JsonUtils {
    public static String toJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    public static void writeToJson(ServletResponse response, ErrorCode errorCode)
            throws IOException {
        ResultObject resp = new ResultObject();
        resp.setErrCode(errorCode);

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(JsonUtils.toJson(resp));
    }
}

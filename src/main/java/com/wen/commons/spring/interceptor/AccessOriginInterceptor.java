package com.wen.commons.spring.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 设置允许跨域访问
 * 
 * @author denis.huang
 * @since 2016年12月11日
 */
public class AccessOriginInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type,Authorization,Accept,X-Requested-With,x-token");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		return super.preHandle(request, response, handler);
	}
}

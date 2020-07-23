package com.wen.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author tim.tang
 * @date 2016年4月21日
 */
public class WebUtil
{
    private static Logger logger = LoggerFactory.getLogger(WebUtil.class);

    private static Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.\\d{1,3}");

    /**
     * 获取cookie值
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName)
    {
        if (cookieName != null)
        {
            Cookie cookie = getCookie(request, cookieName);
            if (cookie != null)
            {
                return cookie.getValue();
            }
        }

        return null;
    }

    /**
     * 获取cookie对象
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static Cookie getCookie(HttpServletRequest request, String cookieName)
    {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0)
        {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }

        return null;
    }

    /**
     * 创建cookie
     *
     * @param response
     * @param name
     * @param value
     * @return
     */
    public static void addCookie(HttpServletResponse response, String name, String value)
    {
        Cookie cookie = new Cookie(name, value);
        response.addCookie(cookie);
    }

    /**
     * 抽取请求的header和rquest params信息
     *
     * @param request
     * @return
     */
    public static String extractRequestData(HttpServletRequest request)
    {
        StringBuilder sb = new StringBuilder();

        Map<String, String> headers = getAllHeaders(request);
        sb.append("======= request headers ====== \n");
        for (Map.Entry<String, String> entry : headers.entrySet())
        {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        Map<String, String> params = getAllParameters(request);
        sb.append("======= request params ======= \n");
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        return sb.toString();
    }

    /**
     * 请求头信息
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllHeaders(HttpServletRequest request)
    {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements())
        {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }

        return headers;
    }

    /**
     * 请求参数列表
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllParameters(HttpServletRequest request)
    {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements())
        {
            String name = paramNames.nextElement();
            params.put(name, request.getParameter(name));
        }

        return params;
    }

    /**
     * 获取请求路径
     *
     * @param request
     * @return
     */
    public static String getRequestPath(HttpServletRequest request)
    {
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();
        if (pathInfo != null)
        {
            path += pathInfo;
        }

        return path;
    }

    /**
     * 从请求头判断cookie domain
     *
     * @param request
     * @return
     */
    public static String getCookieDomain(HttpServletRequest request)
    {
        String domain = null;

        String host = request.getHeader("Referer");
        if (StringUtils.isEmpty(host))
        {
            // get from Host
            host = request.getHeader("Host");
        }
        else
        {
            // 去除 http://
            int ind = host.indexOf("/");
            host = host.substring(ind + 2);
            ind = host.indexOf("/");
            if (ind > 0)
            {
                host = host.substring(0, ind);
            }
        }

        // 端口号去除
        int portInd = host.indexOf(":");
        if (portInd > 0)
        {
            host = host.substring(0, portInd);
        }

        // 支持localhost等
        int index = host.indexOf(".");
        if (index > 0)
        {
            // 是否是IP
            if (!isIpAddress(host))
            {
                int lastIndex = host.lastIndexOf(".");
                if (lastIndex > index)
                {
                    // 至少两个.
                    domain = host.substring(index);
                }
                else
                {
                    // 只有一个.
                    domain = "." + host;
                }
            }
        }

        logger.debug("getCookieDomain: host={}|domain={}", host, domain);

        return domain;
    }

    /**
     * 是否是IP地址(类似192.168.1.1)
     *
     * @param ip
     * @return
     */
    public static boolean isIpAddress(String ip)
    {
        return IP_PATTERN.matcher(ip).matches();
    }

    /**
     * 获取用户网关ip
     *
     * @param request
     * @return
     */
    public static String getClientIp(HttpServletRequest request)
    {
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isEmpty(ip))
        {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0)
        {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
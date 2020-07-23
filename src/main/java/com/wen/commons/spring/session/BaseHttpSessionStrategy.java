package com.wen.commons.spring.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.web.http.HttpSessionStrategy;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tim.tang
 * @date 2015年7月29日
 */
public abstract class BaseHttpSessionStrategy implements HttpSessionStrategy {
    private static final String SESSION_ID_ATTR = "ATTR_REQUESTED_SESSION_ID";
    private static final String SESSION_ID_SET_ATTR = "ATTR_REQUESTED_SESSION_ID_SET";

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getRequestedSessionId(HttpServletRequest request) {
        // get from attr
        String sessionId = (String) request.getAttribute(SESSION_ID_ATTR);
        if (sessionId != null) {
            return sessionId;
        }

        // 有可能已经设置需要返回null
        if (request.getAttribute(SESSION_ID_SET_ATTR) != null) {
            return null;
        }

        // get from request
        sessionId = getSessionId(request);

        if (logger.isDebugEnabled()) {
            String path = request.getServletPath();
            logger.debug("getRequestedSessionId: sessionId={}|path={}", sessionId, path);
        }

        // 保存
        request.setAttribute(SESSION_ID_SET_ATTR, Boolean.TRUE);
        request.setAttribute(SESSION_ID_ATTR, sessionId);

        return sessionId;
    }

    /**
     * 从request中获取sessionId
     *
     * @param request
     * @return
     */
    protected abstract String getSessionId(HttpServletRequest request);

}
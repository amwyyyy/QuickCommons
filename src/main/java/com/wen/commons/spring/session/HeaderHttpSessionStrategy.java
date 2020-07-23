package com.wen.commons.spring.session;

import org.springframework.session.Session;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 使用header记录sessionId
 *
 * @author tim.tang
 * @date 2015年7月29日
 */
public class HeaderHttpSessionStrategy extends BaseHttpSessionStrategy {
    private String headerName = SessionConst.SESSION_HEADER_NAME;

    @Override
    protected String getSessionId(HttpServletRequest request) {
        return request.getHeader(headerName);
    }

    @Override
    public void onNewSession(Session session, HttpServletRequest request,
                             HttpServletResponse response) {
        String sessionId = session.getId();
        response.setHeader(headerName, sessionId);

        logger.debug("onNewSession: sessionId={}", sessionId);
    }

    @Override
    public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("onInvalidateSession");
        response.setHeader(headerName, "");
    }

    /**
     * The name of the header to obtain the session id from. Default is "x-auth-token".
     *
     * @param headerName the name of the header to obtain the session id from.
     */
    public void setHeaderName(String headerName) {
        Assert.notNull(headerName, "headerName cannot be null");
        this.headerName = headerName;
    }
}

package com.wen.commons.spring.session;

import com.wen.commons.utils.StringUtils;
import com.wen.commons.utils.WebUtil;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.session.Session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 使用cookie记录session
 *
 * @author tim.tang
 * @date 2015年7月29日
 */
public class CookieHttpSessionStrategy extends BaseHttpSessionStrategy {
    private static final String cookiePath = SessionConst.DEFAULT_COOKIE_PATH;
    private String cookieName = SessionConst.COMMON_SESSION_COOKIE_NAME;
    private String cookieDomain = null;

    public CookieHttpSessionStrategy() {
        this(null);
    }

    public CookieHttpSessionStrategy(String cookieName) {
        if (StringUtils.isNotEmpty(cookieName)) {
            this.cookieName = cookieName;
        }
    }

    @Override
    protected String getSessionId(HttpServletRequest request) {
        Cookie session = getCookie(request, getCookieName(request));
        return (session == null ? null : session.getValue());
    }

    /**
     * @return
     */
    protected String getCookieName(HttpServletRequest request) {
        return cookieName;
    }

    @Override
    public void onNewSession(Session session, HttpServletRequest request,
                             HttpServletResponse response) {
        String sessionId = session.getId();
        // set cookie
        Cookie sessionCookie = createSessionCookie(request, sessionId);

        // 是否设置过期时间
        int maxAge = getCookieMaxAge(request);
        if (maxAge > 0) {
            sessionCookie.setMaxAge(maxAge);
        }

        response.addCookie(sessionCookie);

        logger.debug("onNewSession: maxAge={}|sessionId={}", maxAge, sessionId);
    }

    /**
     * @param request
     * @return
     */
    private int getCookieMaxAge(HttpServletRequest request) {
        // cookie max age存储在attribute中
        Object obj = request.getAttribute(SessionConst.ATTR_SESSION_COOKIE_MAX_AGE);

        int val = 0;
        if (obj != null) {
            val = (int) ConvertUtils.convert(obj.toString(), Integer.class);
        }

        return val;
    }

    private Cookie createSessionCookie(HttpServletRequest request, String sessionId) {
        String domain = getCookieDomain(request);

        Cookie sessionCookie = new Cookie(getCookieName(request), sessionId);

        sessionCookie.setSecure(request.isSecure());
        sessionCookie.setPath(getCookiePath(request));

        if (StringUtils.isNotEmpty(domain)) {
            sessionCookie.setDomain(domain);
        }

        if (StringUtils.isEmpty(sessionId)) {
            // 删除cookie
            sessionCookie.setMaxAge(0);
            sessionCookie.setValue(null);
        }

        return sessionCookie;
    }

    /**
     * @param request
     * @return
     */
    private String getCookieDomain(HttpServletRequest request) {
        String domain = cookieDomain;

        if (StringUtils.isEmpty(domain)) {
            domain = WebUtil.getCookieDomain(request);
        }

        return domain;
    }

    private String getCookiePath(HttpServletRequest request) {
        return cookiePath;
    }

    protected Cookie getCookie(HttpServletRequest request, String name) {
        return WebUtil.getCookie(request, name);
    }

    @Override
    public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
        // 删除cookie
        Cookie sessionCookie = createSessionCookie(request, null);
        response.addCookie(sessionCookie);

        logger.debug("onInvalidateSession");
    }
}

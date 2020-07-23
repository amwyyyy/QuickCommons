package com.wen.commons.spring.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.Session;
import org.springframework.session.web.http.HttpSessionStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 根据用户传入的客户端类型参数来选择不同的session跟踪策略. 现支持两种策略:<br/>
 * <ul>
 * <li>基于标准Set-Cookie, 用于浏览器客户端</li>
 * <li>基于RestFul的header</li>
 * </ul>
 * 默认用http参数名为_c的值来区分不同的策略, 比如_c=c表示基于cookie, _c=h表示基于header
 *
 * @autor tony.li
 * @date 2015/2/9
 */
public class CompositionSessionStrategy implements HttpSessionStrategy {
    private static Logger logger = LoggerFactory.getLogger(CompositionSessionStrategy.class);

    private static final String clientTypeParam = SessionConst.DEFAULT_CLIENT_TYPE_PARAM;

    private Map<String, HttpSessionStrategy> strategyMap;
    private HttpSessionStrategy defaultStrategy;

    public CompositionSessionStrategy() {
        CookieHttpSessionStrategy cookieStrategy = new CookieHttpSessionStrategy();
        initStrateMap(cookieStrategy);
    }

    public CompositionSessionStrategy(String sessionCookieName) {
        logger.info("CompositionSessionStrategy: sessionCookieName={}", sessionCookieName);

        CookieHttpSessionStrategy cookieStrategy = new CookieHttpSessionStrategy(sessionCookieName);

        initStrateMap(cookieStrategy);
    }

    /**
     * @param cookieStrategy
     */
    private void initStrateMap(CookieHttpSessionStrategy cookieStrategy) {
        strategyMap = new HashMap<>();
        // header
        HeaderHttpSessionStrategy headerStrategy = new HeaderHttpSessionStrategy();
        strategyMap.put(SessionConst.TYPE_HEADER_IOS_VALUE, headerStrategy);
        strategyMap.put(SessionConst.TYPE_HEADER_ANDROID_VALUE, headerStrategy);

        // cookie strategy as default
        defaultStrategy = cookieStrategy;
    }

    @Override
    public String getRequestedSessionId(HttpServletRequest request) {
        return getStrategyByType(request).getRequestedSessionId(request);
    }

    @Override
    public void onNewSession(Session session, HttpServletRequest request, HttpServletResponse response) {
        getStrategyByType(request).onNewSession(session, request, response);
    }

    @Override
    public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
        getStrategyByType(request).onInvalidateSession(request, response);
    }

    private HttpSessionStrategy getStrategyByType(HttpServletRequest request) {
        String ct = request.getHeader(clientTypeParam);

        HttpSessionStrategy strategy = strategyMap.get(ct);

        return strategy != null ? strategy : defaultStrategy;
    }
}

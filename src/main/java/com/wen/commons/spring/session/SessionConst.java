package com.wen.commons.spring.session;

/**
 * @author denis.huang
 * @since 2017/4/17
 */
public class SessionConst {
    /** 识别客户端类型的参数名 */
    public static final String DEFAULT_CLIENT_TYPE_PARAM = "_c";

    public static final String TYPE_HEADER_IOS_VALUE = "ios";
    public static final String TYPE_HEADER_ANDROID_VALUE = "android";

    /** session key 登录后保存用户对象 */
    public static final String ATTR_SESSION_USER = "SESSION_USER";

    /** 存放session最大过期时间的属性键 */
    public static final String ATTR_SESSION_COOKIE_MAX_AGE = "SESSION_COOKIE_MAX_AGE";

    /** 客户端类型 */
    public static final String ATTR_USER_PLATFORM = "USER_PLATFORM";

    /** 客户端UA头信息 */
    public static final String ATTR_USER_AGENT = "USER_AGENT";

    /** 旧的sessionId */
    public static final String ATTR_OLD_SESSION_ID = "OLD_SESSION_ID";

    /** 默认cookie 路径 */
    public static final String DEFAULT_COOKIE_PATH = "/";

    /** 保存会话sessionId的header name */
    public static final String SESSION_HEADER_NAME = "x-auth-token";

    /** 通用的保持会话的cookie name */
    public static final String COMMON_SESSION_COOKIE_NAME = "_s_";
}

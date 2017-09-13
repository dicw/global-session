package org.singledog.global.session;

import org.singledog.global.session.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WrappedRequest extends HttpServletRequestWrapper {
    private static final Logger logger = LoggerFactory.getLogger(WrappedRequest.class);
    private static final long SESSION_TIME_OUT_SECOND = TimeUnit.DAYS.toMinutes(30);//30天

    private HttpServletResponse response;
    private HttpSession session;
    private ServletContext servletContext;
    @SuppressWarnings("unchecked")
    private RedisTemplate<String, String> redisTemplate = SpringContextUtil.getBean("redisTemplate", RedisTemplate.class);
    private HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
    private String cookieName;

    public WrappedRequest(HttpServletRequest request, HttpServletResponse response) {
        super(request);
        this.response = response;
        cookieName = SessionCookieNameHelper.cookieName(request);
        servletContext = request.getSession().getServletContext();
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (session != null)
            return session;

        try {
            String sessionId = this.findSessionId();
            if (sessionId != null) {
                String cacheKey = CacheKeyConstants.H_SESSION + sessionId;
                //check if session meta info exists, if not has meta info, session is not valid
                boolean exists = hashOperations.hasKey(cacheKey, CacheKeyConstants.H_K_SESSION_META);
                if (exists) {
                    //get meta info
                    String metaJson = hashOperations.get(cacheKey, CacheKeyConstants.H_K_SESSION_META);
                    if (!StringUtils.isEmpty(metaJson)) {
                        WrappedSessionMeta meta = JsonUtil.fromJson(metaJson, WrappedSessionMeta.class);
                        WrappedSession wrappedSession = new WrappedSession(meta);
                        wrappedSession.setId(sessionId);
                        wrappedSession.setNew(false);
                        wrappedSession.setLastAccessTime(System.currentTimeMillis());
                        wrappedSession.setServletContext(servletContext);
                        //更新session meta
                        hashOperations.put(cacheKey, CacheKeyConstants.H_K_SESSION_META, JsonUtil.toJson(wrappedSession.extractMeta()));
                        //给session延时
                        hashOperations.getOperations().expire(cacheKey, SESSION_TIME_OUT_SECOND, TimeUnit.MINUTES);
                        this.session = wrappedSession;
                        return session;
                    }
                }
            }

            logger.debug("can not found session ! sessionId : {}", sessionId);
            if (create) {//create a new session
                sessionId = UUID.randomUUID().toString();//generate new sessionID
                logger.debug("create new session ! sessionId : {}", sessionId);
                WrappedSessionMeta meta = new WrappedSessionMeta();
                meta.setNew(true);
                meta.setId(sessionId);
                WrappedSession wrappedSession = new WrappedSession(meta);
                wrappedSession.setServletContext(servletContext);

                String cacheKey = CacheKeyConstants.H_SESSION + sessionId;
                // cache session meta
                hashOperations.put(cacheKey, CacheKeyConstants.H_K_SESSION_META, JsonUtil.toJson(meta));
                // set session expire
                hashOperations.getOperations().expire(wrappedSession.getCacheKey(), SESSION_TIME_OUT_SECOND, TimeUnit.MINUTES);

                Cookie cookie = new Cookie(cookieName, sessionId);
                cookie.setPath("/");
//				cookie.setHttpOnly(true);
                cookie.setMaxAge(Long.valueOf(SESSION_TIME_OUT_SECOND).intValue());
                response.addCookie(cookie);
                this.session = wrappedSession;
                return session;
            }
        } catch (Exception e) {
            logger.error("getSession error !!");
            logger.error(e.getMessage(), e);
        }

        return session;
    }

    @Override
    public HttpSession getSession() {
        return this.getSession(true);
    }

    private String findSessionId() {
        String sessionId = null;
        Cookie[] cookies = super.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    logger.debug("found sessionId : {}", sessionId);
                    break;
                }
            }
        }

        return sessionId;
    }

    @Override
    public String getRemoteAddr() {
        String ip = this.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = this.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = this.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = this.getHeader("X-Real-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = super.getRemoteAddr();
        }
        return ip;
    }

}

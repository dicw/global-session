package org.singledog.global.session;

public interface CacheKeyConstants {
    /**
     * 这是每一个session存在于redis里面的key
     */
    String H_SESSION = "session_";
    /**
     * session的meta信息
     */
    String H_K_SESSION_META = "_meta_";
}
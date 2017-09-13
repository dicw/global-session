package org.singledog.global.session;

import javax.servlet.http.HttpSession;

/**
 * Created by Adam on 2017/8/15.
 */
public class SessionHolder {

    private static final ThreadLocal<HttpSession> sessionLocal = new ThreadLocal<>();

    static void set(HttpSession session) {
        sessionLocal.set(session);
    }

    public static HttpSession get() {
        return sessionLocal.get();
    }

}

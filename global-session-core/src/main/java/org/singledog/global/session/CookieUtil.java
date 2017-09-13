package org.singledog.global.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Adam on 2017/8/15.
 */
public class CookieUtil {
    private static final String cookie_val_attr = "_cookie_val_";

    public static void addMemoryCookie(HttpServletResponse response, String name, String value) {
        addPersistCookie(response, name, value, -1);
    }

    public static void addPersistCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
//        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void addCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
//        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name))
                return cookie.getValue();
        }

        return null;
    }

}

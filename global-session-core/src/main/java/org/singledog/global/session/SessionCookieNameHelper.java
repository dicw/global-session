package org.singledog.global.session;

import org.singledog.global.session.util.EncryptUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Adam on 2017/8/15.
 */
public class SessionCookieNameHelper {

    public static String cookieName(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String acceptEncoding = "gzip, deflate";//request.getHeader("Accept-Encoding"); //this header will changes according to request method (get/post)
        String acceptLanguage = request.getHeader("Accept-Language");
        String accept = "Accept";//request.getHeader("Accept");
        String ip = "localhost";//request.getRemoteAddr();//not reliable
        StringBuilder sb = new StringBuilder(userAgent)
                .append(acceptEncoding)
                .append(acceptLanguage)
                .append(accept)
                .append(ip);
        return EncryptUtil.MD5(sb.toString());
    }
}

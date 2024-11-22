package org.userservice.userservice.utils;

import jakarta.servlet.http.Cookie;

public class CookieUtils {
    public static Cookie createCookie(String key, String value, Integer expiredS) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(expiredS);
        return cookie;
    }
}
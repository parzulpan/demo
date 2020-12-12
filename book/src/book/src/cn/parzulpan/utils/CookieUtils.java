package cn.parzulpan.utils;

import javax.servlet.http.Cookie;

/**
 * @Author : parzulpan
 * @Time : 2020-12-12
 * @Desc :
 */

public class CookieUtils {

    public static Cookie findCookie(String name, Cookie[] cookies) {
        if (name == null || cookies == null || cookies.length == 0) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }

        return null;
    }
}

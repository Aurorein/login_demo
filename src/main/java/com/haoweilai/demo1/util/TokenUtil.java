package com.haoweilai.demo1.util;

import javax.servlet.http.HttpServletRequest;

public class TokenUtil {

    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        return token;
    }
}

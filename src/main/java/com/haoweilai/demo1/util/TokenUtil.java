package com.haoweilai.demo1.util;

import javax.servlet.http.HttpServletRequest;

public class TokenUtil {

    private static final String ACCESS_TOKEN = "Access-Token";
    private static final String REFRESH_TOKEN = "Refresh-Token";

    public static String getAccessToken(HttpServletRequest request) {
        String token = request.getHeader(ACCESS_TOKEN);
        return token;
    }

    public static String getRefreshToken(HttpServletRequest request) {
        String token = request.getHeader(REFRESH_TOKEN);
        return token;
    }
}

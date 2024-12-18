package com.haoweilai.demo1.util;

import org.springframework.util.DigestUtils;

public class MD5Util {
    public static String md5(String src) {
        return DigestUtils.md5DigestAsHex(src.getBytes());
    }
}

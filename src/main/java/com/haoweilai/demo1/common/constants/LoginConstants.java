package com.haoweilai.demo1.common.constants;

import java.util.regex.Pattern;

public class LoginConstants {
    public static final String LOCKED = "LOCKED";
    public static final String COUNT = "COUNT";
    public static final Pattern EMAIL_ENGLISH_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
}

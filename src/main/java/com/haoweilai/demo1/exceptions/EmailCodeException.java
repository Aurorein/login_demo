package com.haoweilai.demo1.exceptions;

public class EmailCodeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EmailCodeException() {
        super("验证码错误！");
    }
}

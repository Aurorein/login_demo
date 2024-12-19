package com.haoweilai.demo1.exceptions;

public class EmailCodeExpireException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EmailCodeExpireException() {
        super("验证码失效！");
    }
}

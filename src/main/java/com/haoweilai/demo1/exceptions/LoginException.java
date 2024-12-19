package com.haoweilai.demo1.exceptions;

public class LoginException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LoginException() {
        super("请重新登录！");
    }
}

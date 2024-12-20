package com.haoweilai.demo1.exceptions;

public class PasswordAuthException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PasswordAuthException() {
        super("密码错误！");
    }
}

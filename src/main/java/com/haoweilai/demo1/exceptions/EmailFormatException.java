package com.haoweilai.demo1.exceptions;

public class EmailFormatException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EmailFormatException() {
        super("邮箱格式错误！");
    }
}

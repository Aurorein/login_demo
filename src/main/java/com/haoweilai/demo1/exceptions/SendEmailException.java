package com.haoweilai.demo1.exceptions;

public class SendEmailException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SendEmailException() {
        super("邮箱验证码发送失败！");
    }
}

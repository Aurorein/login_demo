package com.haoweilai.demo1.exceptions;

public class AgrumentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AgrumentException() {
        super("参数校验错误！");
    }
}

package com.haoweilai.demo1.exceptions;

public class LockedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LockedException() {
        super("账号被锁定");
    }
}

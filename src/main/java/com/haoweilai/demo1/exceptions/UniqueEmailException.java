package com.haoweilai.demo1.exceptions;

public class UniqueEmailException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UniqueEmailException() {
        super("邮箱重复");
    }
}

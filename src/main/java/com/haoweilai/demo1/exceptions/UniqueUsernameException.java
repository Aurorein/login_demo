package com.haoweilai.demo1.exceptions;

public class UniqueUsernameException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UniqueUsernameException() {
        super("用户名重复！");
    }
}

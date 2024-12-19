package com.haoweilai.demo1.vo;

import lombok.Data;

@Data
public class RegisterReq {
    private String username;
    private String password;
    private String accountType;
    private String email;
}

package com.haoweilai.demo1.vo;

import lombok.Data;

@Data
public class LoginReq {
    private String username;
    private String password;
    private Integer loginType;
    private Integer deviceType;

}

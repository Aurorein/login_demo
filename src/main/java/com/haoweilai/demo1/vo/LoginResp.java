package com.haoweilai.demo1.vo;

import lombok.Data;

@Data
public class LoginResp {
    private String token;
    private Long userId;
    private String username;

}

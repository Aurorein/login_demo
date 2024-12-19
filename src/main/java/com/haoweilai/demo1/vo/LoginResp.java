package com.haoweilai.demo1.vo;

import lombok.Data;

@Data
public class LoginResp {
    private String accessToken;
    private String refreshToken;
    private Long userId;

}

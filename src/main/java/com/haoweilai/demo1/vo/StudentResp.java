package com.haoweilai.demo1.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResp {
    private Long id;

    private String username;

    private String email;

    private String avatar;

}

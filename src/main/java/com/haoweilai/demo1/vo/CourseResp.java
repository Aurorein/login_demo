package com.haoweilai.demo1.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseResp {
    private Long id;

    private String courseName;

    private String descrpt;

    private LocalDateTime courseTime;
}

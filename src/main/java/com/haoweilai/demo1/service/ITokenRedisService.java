package com.haoweilai.demo1.service;

import com.haoweilai.demo1.model.Student;

public interface ITokenRedisService {
    String setStu(Student stu, String token);

    Student getStu(String token);

    String getSessionIp(String token, Integer deviceType);

    void addSession(Integer deviceType, String token, String ip);

    boolean increLocked(String userId, String ip);

    boolean isLocked(String userId, String ip);

    void saveCode(String email, String code);

    String getCode(String email);
}

package com.haoweilai.demo1.service.impl;

import com.haoweilai.demo1.model.Student;
import com.haoweilai.demo1.service.ITokenRedisService;
import com.haoweilai.demo1.util.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class TokenRedisService implements ITokenRedisService {

    @Autowired
    private RedisRepository redisRepository;

    @Override
    public String setStu(Student stu, String token) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", stu.getUsername());
        map.put("password", stu.getPassword());
        map.put("avatar", stu.getAvatar());
        map.put("email", stu.getEmail());

        redisRepository.opsForHash().putAll(token, map);
        redisRepository.setExpire(token, 15, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public Student getStu(String token) {
        Student student = new Student();
        student.setUsername((String) redisRepository.opsForHash().get(token, "username"));
        student.setPassword((String) redisRepository.opsForHash().get(token, "password"));
        student.setAvatar((String) redisRepository.opsForHash().get(token, "avatar"));
        student.setEmail((String) redisRepository.opsForHash().get(token, "email"));
        return student;
    }

    @Override
    // 可能为null,如果不为null则说明已经在该设备登录过了
    public String getSessionIp(String token, Integer deviceType) {
        return (String) redisRepository.opsForHash().get("session_" +token, String.valueOf(deviceType));
    }

    @Override
    public void addSession(Integer deviceType, String token, String ip) {
        HashMap<String, Object> sessionMap = new HashMap<>();
        sessionMap.put(String.valueOf(deviceType), ip);
        redisRepository.opsForHash().putAll("session_" + token, sessionMap);
        // redis挂了？

    }

    @Override
    // 返回是否要锁定
    public boolean increLocked(String userId, String ip) {
        String val = (String) redisRepository.get("lock_" + userId);
        if(val == null) {
            redisRepository.set("lock_" + userId + "_" + ip, "1_" + new Date().toString());
            return false;
        } else {
            String[] split = val.split("_");
            Integer locked = Integer.valueOf(split[1]);
            String date = split[2];
            locked += 1;
            redisRepository.set("lock_" + userId + "_" + ip, String.valueOf(locked) + "_" + date);
            if(locked >= 5) {
                return true;
            } else {
                return false;
            }
        }
    }

    // 是否需要被锁住
    @Override
    public boolean isLocked(String userId, String ip) {
        String val = (String) redisRepository.get("lock_" + ip);
        String[] split = val.split("_");
        Integer locked = Integer.valueOf(split[0]);
        String date = split[1];

        if(locked < 5) {
            return false;
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //当前时间
            Date beginTime = new Date();
            //利用时间格式，把当前时间转为字符串
            String start = df.format(beginTime);
            Long begin = beginTime.getTime();
            Date endTime = null;
            try {
                endTime = df.parse(date);
            } catch (ParseException e) {
                // 错误处理
            }
            if(endTime != null && beginTime != null && endTime.before(beginTime)) {
                if(endTime.getTime() - beginTime.getTime() < 5 * 60 * 1000) {
                    return true;
                } else {
                    redisRepository.set("lock_" + userId + "_" + ip,  "1_" + new Date());
                    return false;
                }
            } else {
                return false;
            }

        }
    }

    @Override
    public void saveCode(String email, String code) {
        redisRepository.setExpire(email, code, 60);
    }

    @Override
    public String getCode(String email) {
        if(redisRepository.exists(email)) {
            return (String) redisRepository.get(email);
        } else {
            return null;
        }
    }


}

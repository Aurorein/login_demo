package com.haoweilai.demo1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haoweilai.demo1.common.ResultData;
import com.haoweilai.demo1.model.Student;
import com.haoweilai.demo1.dao.StudentMapper;
import com.haoweilai.demo1.service.IStudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoweilai.demo1.service.ITokenRedisService;
import com.haoweilai.demo1.util.*;
import com.haoweilai.demo1.vo.LoginReq;
import com.haoweilai.demo1.vo.LoginResp;
import com.haoweilai.demo1.vo.StudentReq;
import com.haoweilai.demo1.vo.StudentResp;
import io.swagger.annotations.Authorization;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author cuixuening
 * @since 2024-12-18
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ITokenRedisService tokenRedisService;

    @Autowired
    private HttpServletRequest request;

    @Override
    public LoginResp login(LoginReq loginReq) {
        String username = loginReq.getUsername();
        String password = MD5Util.md5(loginReq.getPassword()).toLowerCase();
        Student stu;
        if (loginReq.getLoginType() == 1) {
            // 1. 账号密码登录验证
            QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
            studentQueryWrapper.eq("username", username);
            stu = studentMapper.selectOne(studentQueryWrapper);
        } else {
            // 2. 邮箱登录
            QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
            studentQueryWrapper.eq("email", username);
            stu = studentMapper.selectOne(studentQueryWrapper);

            // 生成验证码
            String code = String.valueOf(ValidateCodeUtils.generateValidateCode(4));

            // 发送邮箱

            // 验证码缓存到redis

        };
        String ip = IpUtil.getIpAddr(request);

        if(tokenRedisService.isLocked(String.valueOf(stu.getId()), ip)) {
            // 返回已被锁定

        }

        if(stu == null) {
            // 用户名错误

        }
        if(password != stu.getPassword()) {
            // 密码错误
            // 增加错误次数

            boolean isLocked = tokenRedisService.increLocked(String.valueOf(stu.getId()), ip);
            if(isLocked) {
                // 添加到黑名单了，返回已被锁定

            }
        }
        // 登录认证成功，生成token，存入redis
        String token = MD5Util.md5(password).toLowerCase();
        tokenRedisService.setStu(stu, token);

        String sessionIp = tokenRedisService.getSessionIp(token, loginReq.getDeviceType());
        if(StringUtils.isNotBlank(sessionIp)) {
            // 踢人下线
        }
        tokenRedisService.addSession(loginReq.getDeviceType(), token, ip);

    }

    public StudentResp editStu(@RequestBody StudentReq studentReq) {
        // 先修改数据库
        Student student = new Student();
        BeanUtils.copyProperties(studentReq, student);
        student.setUpdateTime(LocalDateTime.now());
        studentMapper.updateById(student);
        StudentResp studentResp = new StudentResp();

        String token = TokenUtil.getToken(request);
        // 修改缓存
        tokenRedisService.setStu(student, token);

        BeanUtils.copyProperties(student, studentResp);
        return studentResp;
    }
}

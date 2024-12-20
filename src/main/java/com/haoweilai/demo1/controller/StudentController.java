package com.haoweilai.demo1.controller;

import com.haoweilai.demo1.common.ResultData;
import com.haoweilai.demo1.common.constants.AccountType;
import com.haoweilai.demo1.common.constants.LoginConstants;
import com.haoweilai.demo1.exceptions.AgrumentException;
import com.haoweilai.demo1.exceptions.EmailFormatException;
import com.haoweilai.demo1.model.Student;
import com.haoweilai.demo1.service.IStudentService;
import com.haoweilai.demo1.util.MD5Util;
import com.haoweilai.demo1.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author cuixuening
 * @since 2024-12-18
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    IStudentService studentService;

    @GetMapping("/list")
    public ResultData<List<Student>> list() {
        return ResultData.success(studentService.list());
    }

    @PostMapping("login")
    public ResultData<LoginResp> login(@RequestBody LoginReq loginReq) {
        // 1. 登录方式，1是账号密码登录，2是邮箱登录
        // 参数验证

        if(!AccountType.PASSWORD.equals(loginReq.getAccountType()) && !AccountType.EMAIL.equals(loginReq.getAccountType())) {
            throw new AgrumentException();
        }

        switch(loginReq.getAccountType()) {
            case AccountType.PASSWORD : {
                if(StringUtils.isBlank(loginReq.getPassword())) {
                    throw new RuntimeException();
                }
                if(StringUtils.isBlank(loginReq.getUsername())) {
                    throw new RuntimeException();
                }
                break;
            }

            case AccountType.EMAIL: {
                if(StringUtils.isBlank(loginReq.getUsername())) {
                    throw new AgrumentException();
                }
                if(!LoginConstants.EMAIL_ENGLISH_PATTERN.matcher(loginReq.getUsername()).matches()) {
                    throw new EmailFormatException();
                }
                break;
            }
        }
        return ResultData.success(studentService.login(loginReq));
    }

    @GetMapping("/get")
    public ResultData<StudentResp> getByToken() {
        // 参数验证
        Student student = studentService.getByToken();
        StudentResp studentResp = new StudentResp();
        BeanUtils.copyProperties(student, studentResp);
        return ResultData.success(studentResp);
    }

    @PostMapping("/edit")
    public ResultData<StudentResp> edit(@RequestBody StudentReq studentReq) {
        StudentResp studentResp = studentService.editStu(studentReq);
        return ResultData.success(studentResp);
    }

    @PostMapping("/register")
    public ResultData<RegisterResp> register(@RequestBody RegisterReq registerReq) {
        RegisterResp register = studentService.register(registerReq);
        return ResultData.success(register);
    }


}

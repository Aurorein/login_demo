package com.haoweilai.demo1.controller;

import com.haoweilai.demo1.common.ResultData;
import com.haoweilai.demo1.model.Student;
import com.haoweilai.demo1.service.IStudentService;
import com.haoweilai.demo1.util.MD5Util;
import com.haoweilai.demo1.vo.LoginReq;
import com.haoweilai.demo1.vo.LoginResp;
import org.apache.commons.lang3.StringUtils;
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

    @GetMapping("list")
    public List<Student> hello() {
        return studentService.list();
    }

    @PostMapping("login")
    public ResultData<LoginResp> login(@RequestBody LoginReq loginReq) {
        // 1. 登录方式，1是账号密码登录，2是邮箱登录
        // 参数验证
        if(StringUtils.isEmpty(loginReq.getUsername())) {

        }
        if(StringUtils.isEmpty(loginReq.getPassword())) {

        }
        return ResultData.success(studentService.login(loginReq));
    }


}

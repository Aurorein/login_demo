package com.haoweilai.demo1.controller;

import com.haoweilai.demo1.common.ResultData;
import com.haoweilai.demo1.model.Student;
import com.haoweilai.demo1.service.IStudentService;
import com.haoweilai.demo1.vo.StudentReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    IStudentService studentService;

    @PostMapping("/sendEmail")
    public ResultData<Void> send(@RequestBody StudentReq studentReq) {
        studentService.sendMsg(studentReq);
        return ResultData.success(null);
    }
}

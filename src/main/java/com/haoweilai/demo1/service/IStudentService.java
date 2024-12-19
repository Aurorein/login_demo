package com.haoweilai.demo1.service;

import com.haoweilai.demo1.model.Student;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoweilai.demo1.vo.*;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author cuixuening
 * @since 2024-12-18
 */
public interface IStudentService extends IService<Student> {

    LoginResp login(LoginReq loginReq);

    RegisterResp register(RegisterReq registerReq);

    void logout(String userId);

    String sendMsg(StudentReq studentReq);

    StudentResp editStu(StudentReq studentReq);

    Student getByToken();
}

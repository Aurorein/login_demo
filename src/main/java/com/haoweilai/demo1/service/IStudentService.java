package com.haoweilai.demo1.service;

import com.haoweilai.demo1.model.Student;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoweilai.demo1.vo.LoginReq;
import com.haoweilai.demo1.vo.LoginResp;

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
}

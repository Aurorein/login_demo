package com.haoweilai.demo1.service;

import com.haoweilai.demo1.model.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoweilai.demo1.vo.CourseReq;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author cuixuening
 * @since 2024-12-18
 */
public interface ICourseService extends IService<Course> {

    List<Course> listByTime(Long userId, Date courseTime);
}

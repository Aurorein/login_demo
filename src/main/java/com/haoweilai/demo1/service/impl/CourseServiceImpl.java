package com.haoweilai.demo1.service.impl;

import com.haoweilai.demo1.model.Course;
import com.haoweilai.demo1.dao.CourseMapper;
import com.haoweilai.demo1.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author cuixuening
 * @since 2024-12-18
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<Course> listByTime(Long userId, Date courseTime) {
        return courseMapper.listByTime(userId, courseTime);
    }
}

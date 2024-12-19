package com.haoweilai.demo1.controller;

import com.haoweilai.demo1.model.Course;
import com.haoweilai.demo1.service.ICourseService;
import com.haoweilai.demo1.vo.CourseReq;
import com.haoweilai.demo1.vo.CourseResp;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author cuixuening
 * @since 2024-12-18
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    ICourseService courseService;

    @PostMapping("/listByTime")
    public List<CourseResp> listByTime(@RequestBody CourseReq courseReq) {

        List<Course> courses = courseService.listByTime(courseReq.getUserId(), courseReq.getCourseTime());
        List<CourseResp> courseResps = courses.stream()
                .map(course -> {
                    CourseResp courseResp = new CourseResp();
                    BeanUtils.copyProperties(course, courseResp);
                    return courseResp;
                })
                .collect(Collectors.toList());
        return courseResps;
    }

}

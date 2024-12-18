package com.haoweilai.demo1.dao;

import com.haoweilai.demo1.model.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author cuixuening
 * @since 2024-12-18
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

}

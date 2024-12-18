package com.haoweilai.demo1.dao;

import com.haoweilai.demo1.model.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author cuixuening
 * @since 2024-12-18
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

}

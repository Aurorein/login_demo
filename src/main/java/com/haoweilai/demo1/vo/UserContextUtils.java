package com.haoweilai.demo1.vo;

import com.haoweilai.demo1.model.Student;
import org.springframework.stereotype.Component;

@Component
public class UserContextUtils {

    private ThreadLocal<String> userId = new ThreadLocal<>();

    private ThreadLocal<Student> student = new ThreadLocal<>();

    public String getUserId() {
        return userId.get();
    }

    public void setUserId(String userId) {
        this.userId.set(userId);
    }

    public Student getStudent() {
        return student.get();
    }

    public void setStudent(Student student) {
        this.student.set(student);
    }


}

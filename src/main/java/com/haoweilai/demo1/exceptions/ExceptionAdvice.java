package com.haoweilai.demo1.exceptions;

import com.haoweilai.demo1.common.ResultData;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionAdvice {

    /**
     * 其他异常拦截
     * @param ex 异常
     * @param request 请求参数
     * @return 接口响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultData<Void> handleException(Exception ex, HttpServletRequest request) {
        return ResultData.fail(500, ex.getMessage());
    }
}


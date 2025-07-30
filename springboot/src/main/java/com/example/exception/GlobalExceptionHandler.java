package com.example.exception;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice(basePackages="com.example.controller")
public class GlobalExceptionHandler {

    private static final Log log = LogFactory.get();


    // Global exception handler using @ExceptionHandler, primarily for generic exceptions
    @ExceptionHandler(Exception.class)
    @ResponseBody // Returns a JSON response
    public Result error(HttpServletRequest request, Exception e){
        log.error("error msg：",e);
        return Result.error();
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody // Returns a JSON response
    public Result customError(HttpServletRequest request, CustomException e){
        return Result.error(e.getMsg());
    }
}

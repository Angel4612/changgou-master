package com.changgou.handler;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.exception.ChangGouException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ChangGouBaseExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result ExceptionHandler(HttpServletRequest request, ChangGouException e) {
        e.printStackTrace();
        return new Result(false, StatusCode.ERROR, "出错了!");
    }



}

package com.atguigu.gmall.exception;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


//全局异常处理切面
@RestControllerAdvice
@Slf4j
public class AppGlobalExceptionHandle {

    @Value("${spring.application.name}")
    String applicationName;

    @ExceptionHandler(GmallException.class)
    public Result handleGmallException(GmallException e){
        log.error("全局异常处理之业务异常处理:{}"+e);
        Result fail = Result.fail();
        fail.setCode(e.getCode());
        fail.setMessage(e.getMessage());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        StringBuffer url = request.getRequestURL();
        Map<String, String[]> map = request.getParameterMap();

        Map<String, Object> requestInfo  = new HashMap<>();
        requestInfo.put("path",url);
        requestInfo.put("params",map);
        requestInfo.put("serviceName",applicationName);


        fail.setData(requestInfo);
        return fail;
    }

    @ExceptionHandler(Exception.class)
    public Result handleOtherException(Exception e){
        log.error("全局异常处理之系统异常处理:{}"+e);
        Result fail = Result.fail();
        fail.setCode(500);
        fail.setMessage("服务器内部错误");
        return fail;
    }
}

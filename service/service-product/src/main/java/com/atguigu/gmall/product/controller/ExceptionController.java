package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.exception.AppGlobalExceptionHandle;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/product")
public class ExceptionController {

//    //异常处理
//    @GetMapping("/demo/{num}")
//    public String getNum(@PathVariable Integer num){
//
//        if(num%2 == 0){
//            throw new GmallException(ResultCodeEnum.SECKILL_NO_PAY_ORDER);
//        }
//        return "hello";
//    }
}

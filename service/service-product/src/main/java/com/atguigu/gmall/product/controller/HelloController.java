package com.atguigu.gmall.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/product")
public class HelloController {

    //hello
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}

package com.atguigui.gmall.web.config;

import com.atguigu.gmall.annotation.EnableFeignAuthHeaderInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.cart",
                                    "com.atguigu.gmall.feign.item"  ,
                                    "com.atguigu.gmall.feign.list",
                                    "com.atguigu.gmall.feign.order",
                                    "com.atguigu.gmall.feign.product",
                                    "com.atguigu.gmall.feign.user"})
@EnableFeignAuthHeaderInterceptor
public class AppFeignConfigruation {
}

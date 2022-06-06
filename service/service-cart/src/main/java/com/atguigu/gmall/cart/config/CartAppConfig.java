package com.atguigu.gmall.cart.config;

import com.atguigu.gmall.annotation.EnableAutoDoubleThreadPool;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableAutoDoubleThreadPool
@Configuration
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
public class CartAppConfig {
}

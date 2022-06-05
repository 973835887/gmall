package com.atguigu.gmall.cart.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
public class CartAppConfig {
}

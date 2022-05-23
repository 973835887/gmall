package com.atguigui.gmall.web.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign")
public class AppFeignConfigruation {
}

package com.atguigu.gmall.pay.config;

import com.atguigu.gmall.annotation.EnableFeignAuthHeaderInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients({"com.atguigu.gmall.feign.order"})
@Configuration
@EnableFeignAuthHeaderInterceptor
public class AppConfig {
}

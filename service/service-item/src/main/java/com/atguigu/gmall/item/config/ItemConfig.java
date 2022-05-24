package com.atguigu.gmall.item.config;

import com.atguigu.gmall.annotation.EnableAppAutoRedissonAndCache;
import com.atguigu.gmall.annotation.EnableAutoDoubleThreadPool;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableAppAutoRedissonAndCache
@EnableAutoDoubleThreadPool
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
@Configuration
public class ItemConfig {
}

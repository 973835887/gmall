package com.atguigu.gmall.item.config;

import com.atguigu.gmall.starter.annotation.EnableAppAutoRedissonAndCache;
import com.atguigu.gmall.starter.annotation.EnableAutoCache;
import com.atguigu.gmall.annotation.EnableAutoDoubleThreadPool;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
//@EnableAutoCachecom.atguigu.gmall.annotation.EnableAutoDoubleThreadPool;
//import org.springframework.cloud.openfeign.EnableFeignClients;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.EnableAspectJAutoProxy;
//
//@EnableAspectJAutoProxy
////@EnableAutoCache
////@EnableAppAutoRedissonAndCache
//@EnableAutoDoubleThreadPool
//@EnableFeignClients(basePackages
//@EnableAppAutoRedissonAndCache
@EnableAutoDoubleThreadPool
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
@Configuration
public class ItemConfig {
}

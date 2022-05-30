package com.atguigu.gmall.product.config;

import com.atguigu.gmall.annotation.*;
import com.atguigu.gmall.config.MyBatisPlusConfig;
import com.atguigu.gmall.starter.annotation.EnableAppAutoRedissonAndCache;
import com.atguigu.gmall.starter.annotation.EnableAutoCache;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@EnableMinio
@EnableTransactionManagement
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper")
@Import(MyBatisPlusConfig.class)
@EnableMinio
@EnableAutoExceptionHandle
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.list")
@EnableSwaggerApi
@Configuration
public class AppConfiguration {


}

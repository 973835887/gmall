package com.atguigu.gmall.product.config;

import com.atguigu.gmall.annotation.EnableAutoExceptionHandle;
import com.atguigu.gmall.annotation.EnableMinio;
import com.atguigu.gmall.annotation.EnableSwaggerApi;
import com.atguigu.gmall.config.MyBatisPlusConfig;
import com.atguigu.gmall.minio.config.MinioConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@EnableMinio
@EnableTransactionManagement
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper")
@Import(MyBatisPlusConfig.class)
@EnableMinio
@EnableAutoExceptionHandle
@EnableSwaggerApi
@Configuration
public class AppConfiguration {


}

package com.atguigu.gmall.minio.config;

import com.atguigu.gmall.minio.service.OSSService;
import com.atguigu.gmall.minio.service.impl.OSServiceImpl;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfiguration {

    @Autowired
    MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient minioClient = new MinioClient(minioProperties.endpoint,minioProperties.accessKey,minioProperties.secretKey);
        boolean exists = minioClient.bucketExists(minioProperties.bucket);
        if (!exists){
            minioClient.makeBucket(minioProperties.bucket);
        }
        return minioClient;
    }

    @Bean
    public OSSService ossService(){
        OSServiceImpl service = new OSServiceImpl();
        return service;
    }
}

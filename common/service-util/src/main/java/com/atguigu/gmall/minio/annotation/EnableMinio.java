package com.atguigu.gmall.minio.annotation;

import com.atguigu.gmall.minio.config.MinioConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Import(MinioConfiguration.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableMinio {
}

package com.atguigu.gmall.annotation;

import com.atguigu.gmall.exception.AppGlobalExceptionHandle;
import com.atguigu.gmall.minio.config.MinioConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Import(AppGlobalExceptionHandle.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableAutoExceptionHandle {
}

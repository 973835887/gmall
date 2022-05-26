package com.atguigu.gmall.starter.cache.aop.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    //自定义属性
    //代表缓存用的key
    String value() default "";

    //支持动态表达式
    @AliasFor("value")
    String cacheKey() default "";


//    boolean EnableBloom() default false;

    //传入布隆过滤器的名字
    String bloomName() default "";

    //如果启用布隆过滤器,布隆过滤器判定所用的值
    String bloomValue()  default  "";

}


package com.atguigu.gmall.starter;

import com.atguigu.gmall.starter.annotation.EnableAppAutoRedissonAndCache;
import com.atguigu.gmall.starter.annotation.EnableAutoCache;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;

//缓存的自动配置类
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
@EnableAutoCache
@EnableAppAutoRedissonAndCache
public class CacheStarterAutoConfiguration {
}

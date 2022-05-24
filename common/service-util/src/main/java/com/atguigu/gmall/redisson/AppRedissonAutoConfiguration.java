package com.atguigu.gmall.redisson;


import com.atguigu.gmall.common.constants.RedisConst;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@AutoConfigureAfter(AppRedissonAutoConfiguration.class)
@Configuration
@Slf4j
public class AppRedissonAutoConfiguration {


    @Autowired(required = false)
    List<BloomTask> bloomTask;

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+redisProperties.getHost()+":"+redisProperties.getPort()).setPassword(redisProperties.getPassword());
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    //注入布隆过滤器
    @Bean
    public RBloomFilter<Object> skuIdBloom(RedissonClient redissonClient){
        //1.创建布隆过滤器
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(RedisConst.BLOOM_SKUID_KEY);
        //2.判断布隆之前是否存在 初始化布隆过滤器
        if (filter.isExists()) {
            log.info("redis已经配置好了布隆");
            return filter;
        }else {
            //不存在初始化布隆并添加数据
            log.info("redis正在初始化布隆");
            filter.tryInit(1000000,0.00001);
            for (BloomTask task : bloomTask) {
                if (task instanceof SkuBloomTask){
                    task.initData(filter);
                }
            }
            return filter;
        }

    }
}

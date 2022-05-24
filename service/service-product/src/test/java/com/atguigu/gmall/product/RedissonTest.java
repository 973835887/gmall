package com.atguigu.gmall.product;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedissonTest {

    @Autowired
    RedissonClient redissonClient;

    @Test
    public void redissonTest(){
        System.out.println("redissonClient = " + redissonClient);
    }

    @Test
    public void bloom(){
        //创建bloom过滤器
        RBloomFilter<Object> filter = redissonClient.getBloomFilter("sku:bloom");
        System.out.println("filter.contains(25) = " + filter.contains(25));
        System.out.println("filter.contains(13) = " + filter.contains(13));
    }

    @Test
    public void bloomCreate(){
        //创建bloom过滤器
        RBloomFilter<Object> filter = redissonClient.getBloomFilter("sku:bloom");
        //初始化blomm过滤器 . 设置可放数据大小以及 容错率
        filter.tryInit(1000000,0.00001);

        filter.add(11);
        filter.add(12);
        filter.add(13);

        System.out.println("filter.contains(11) = " + filter.contains(11));
        System.out.println("filter.contains(12) = " + filter.contains(12));
        System.out.println("filter.contains(13) = " + filter.contains(13));
        System.out.println("filter.contains(14) = " + filter.contains(14));

    }
}

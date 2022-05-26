package com.atguigu.gmall.item.controller;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class RedissonController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @GetMapping("/redis/incr/redisson")
    public String incrWithRedisson(){
        RLock rLock = redissonClient.getLock("redisson");
        try {
            rLock.lock();
            System.out.println("处理请求");

            String count = redisTemplate.opsForValue().get("count");

            int i = Integer.parseInt(count);
            i++;
            //修改原值
            redisTemplate.opsForValue().set("count",i + "");

        } finally {
//            if (rLock.isLocked()){
//                rLock.unlock();
//            }
            try {
                rLock.unlock();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("别人的锁");
            }
        }
        return "yes";
    }

    /**
     * 可重入锁 lock.lock
     * 1.默认30秒
     * 2.续期
     * lock.trylock 也会自动续期,默认30秒
     * lock.trylock(带参数) 一定时间内等待,无法自动续期
     * @return
     */
    @GetMapping("/lock/hello")
    public String reentrantLock() throws InterruptedException {
        //得到锁
        RLock lock = redissonClient.getLock("lock-hello");

        //阻塞式枷锁
//        lock.lock();
        //非阻塞式加锁
//        boolean b = lock.tryLock();
//        if (b){
//
//            System.out.println("hello");
//            Thread.sleep(1000*60);
//            //解锁
//            lock.unlock();
//        }
        // 等五秒,五秒之内疯狂重试;10秒后自动解锁(过期) ->有限等待
        boolean b = lock.tryLock(5, 10, TimeUnit.SECONDS);


        return "ok";
    }

    //本地锁多服务还是无法锁住
//    ReentrantLock lock = new ReentrantLock();

    /**
     * 1. 本地未加锁 压测1W请求 redis缓存值 370 :多个线程操作同一个值的i
     * 2.  本地加锁: 单个微服务 同一把锁 压测1W请求 redis缓存值 10000
     * 3. 多服务本地加锁 : 各自的锁 压测1W请求 redis缓存值  5185
     * 4. 分布式锁: 多个微服务 同一把锁 压测1W请求 redis缓存值 10000
     *
     * @return
     */
    @GetMapping("/redis/incr")
    public String incr(){
        System.out.println("处理请求");
        //只有一个人会抢到锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "1", 10, TimeUnit.SECONDS);

        while (!lock){
            //没抢到  继续抢锁
            lock = redisTemplate.opsForValue().setIfAbsent("lock", "1", 10, TimeUnit.SECONDS);
        }

        String count = redisTemplate.opsForValue().get("count");

        int i = Integer.parseInt(count);
        i++;

        redisTemplate.opsForValue().set("count",i + "");

        redisTemplate.delete("lock");

        return "ok";


    }
}

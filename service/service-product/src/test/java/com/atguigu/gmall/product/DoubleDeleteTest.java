package com.atguigu.gmall.product;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.*;

//修改数据之后删除缓存，延迟双删除
@SpringBootTest
public class DoubleDeleteTest {
    @Autowired
    BaseTrademarkMapper baseTrademarkMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    public void Delete2Test(){
        //1.0 之前保存过key的值到缓存中
        String key = "baseTrademark";
        //1.修改数据库的数据
        BaseTrademark baseTrademark = new BaseTrademark();
        baseTrademark.setTmName("飞龙跟文超嘿嘿嘿");
        UpdateWrapper<BaseTrademark> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",13);
        baseTrademarkMapper.update(baseTrademark,updateWrapper);
        //2.修改完成之后删除缓存中有的数据
        delete2Cache(key);
    }

    private void delete2Cache(String key) {
        redisTemplate.delete(key);
        //用线程池定时任务
        //线程不安全的线程池工具类 Executors
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        //10秒之后进行第二次删除
        scheduledThreadPool.schedule(()->{
            redisTemplate.delete(key);
        },10,TimeUnit.SECONDS);

        //Executors.newScheduledThreadPool()  不安全的  自定义线程池无定时任务
        //int corePoolSize,
        //int maximumPoolSize,
        //long keepAliveTime,
        //TimeUnit unit,
        //BlockingQueue<Runnable> workQueue,阻塞队列  可以 最多阻塞1000个
        //ThreadFactory threadFactory,
        //RejectedExecutionHandler handler   ---  CallerRunsPolicy  线程满了之后同步运行知道完毕
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,
//                4,
//                5,
//                TimeUnit.MINUTES,
//                new LinkedBlockingDeque<>(1000),
//                new ThreadFactory() {
//                    @Override
//                    public Thread newThread(Runnable r) {
//                        Thread thread = new Thread(r);
//                        thread.setName(thread.getName());
//                        return thread;
//                    }
//                },
//                new ThreadPoolExecutor.CallerRunsPolicy());



    }
}

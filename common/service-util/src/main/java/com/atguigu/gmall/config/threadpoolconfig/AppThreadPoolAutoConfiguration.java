package com.atguigu.gmall.config.threadpoolconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@EnableConfigurationProperties(AppThreadPoolProperties.class)
@Configuration
@Slf4j
public class AppThreadPoolAutoConfiguration {
    /**
     * int corePoolSize,
     * int maximumPoolSize,
     * long keepAliveTime,
     * TimeUnit unit,
     * BlockingQueue<Runnable> workQueue,
     * ThreadFactory threadFactory,
     * RejectedExecutionHandler handler
     * @return
     */
    //自动注入优先注入
    @Primary
    @Bean
    public ThreadPoolExecutor corePool(AppThreadPoolProperties appThreadPoolProperties, @Value("${spring.application.name:default}") String appName ){
        log.info("业务核心线程池准备完成");
        return new ThreadPoolExecutor(appThreadPoolProperties.getCorePoolSize(),
                appThreadPoolProperties.getMaximumPoolSize(),
                appThreadPoolProperties.getKeepAliveTime(),
                appThreadPoolProperties.getUnit(),
                new LinkedBlockingDeque<>(appThreadPoolProperties.getQueueSize()),
                new AppThreadFactory("["+appName+"]-core"),
                appThreadPoolProperties.getHandler());
    }

    @Bean
    public ThreadPoolExecutor otherPool(AppThreadPoolProperties appThreadPoolProperties, @Value("${spring.application.name:default}") String appName){
        log.info("业务非核心线程池准备完成");
        return new ThreadPoolExecutor(appThreadPoolProperties.getCorePoolSize()/2,
                appThreadPoolProperties.getMaximumPoolSize()/2,
                appThreadPoolProperties.getKeepAliveTime(),
                appThreadPoolProperties.getUnit(),
                new LinkedBlockingDeque<>(appThreadPoolProperties.getQueueSize()/2),
                new AppThreadFactory("["+appName+"]-other"),
                appThreadPoolProperties.getHandler());
    }

}


        //自定义线程工厂
  class AppThreadFactory implements ThreadFactory{
            private String appName;
            private AtomicInteger count = new AtomicInteger(1);
            public AppThreadFactory(String appName) {
                this.appName = appName;
            }

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(appName+count.getAndIncrement());
                return thread;
            }
        }

package com.atguigu.gmall.config.threadpoolconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = "app.threadpool")
public class AppThreadPoolProperties {
    private Integer corePoolSize;
    private Integer maximumPoolSize;
    private Long keepAliveTime;
    private TimeUnit unit = TimeUnit.MINUTES;
    private Integer queueSize;
    private RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
}

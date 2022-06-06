package com.atguigu.gmall.config.threadpoolconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = "app.threadpool")
public class AppThreadPoolProperties {
    private Integer corePoolSize = 4;
    private Integer maximumPoolSize = 4;
    private Long keepAliveTime = 5L;
    private TimeUnit unit = TimeUnit.MINUTES;
    private Integer queueSize = 1000;
    private RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
}

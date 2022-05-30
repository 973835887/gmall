package com.atguigu.gmall.list;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;



//开启es的自动仓库功能;在主启动包以及子包下自动扫描带repository的注解
@EnableElasticsearchRepositories
@SpringCloudApplication
public class ListApplication {
    public static void main(String[] args) {
        SpringApplication.run(ListApplication.class,args);
    }
}

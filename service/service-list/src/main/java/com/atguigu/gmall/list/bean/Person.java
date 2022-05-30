package com.atguigu.gmall.list.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
//shards 分片存储[分库分表],分成几堆进行存储  replica 副本;类似mysql的主从同步
@Document(shards = 1 ,replicas = 1, indexName = "person",createIndex = true)
public class Person {
    @Id
    private Long id;
    @Field
    private String userName;

    private String address;

    @Field(format = DateFormat.custom,pattern = "yyyy-MM-dd")
    private Date birthday;
}

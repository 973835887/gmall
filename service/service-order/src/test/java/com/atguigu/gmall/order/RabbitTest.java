package com.atguigu.gmall.order;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitTest {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void sendTest(){
        //String exchange, String routingKey, Object message
        rabbitTemplate.convertAndSend("hello","h1","给hello交换机发送消息");
    }

    @Test
    void reviceTest(){
        //String exchange, String routingKey, Object message
        Message hello = rabbitTemplate.receive("hello");
        System.out.println("hello = " + hello);
        byte[] body = hello.getBody();
        System.out.println("消息内容"+new String(body));
    }
}

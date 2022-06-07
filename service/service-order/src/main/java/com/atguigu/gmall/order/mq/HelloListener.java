package com.atguigu.gmall.order.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class HelloListener {

    @RabbitListener(queues = "hello")
    public void listener(Message message, Channel channel){
        log.info("收到消息{}",new String(message.getBody()));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        //long deliveryTag, boolean multiple
        try {
            channel.basicAck(deliveryTag,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.atguigu.gmall.order.config;

import com.atguigu.gmall.common.constants.MqConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OrderMqConfig {

    //交换机
    @Bean
    public Exchange orderEventExchange(){
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return  new TopicExchange(MqConst.ORDER_EVENT_EXCHANGE,true,false,null);
    }


    //延时队列
    @Bean
    public Queue orderDelayQueue(){
        //String name,
        // boolean durable,
        // boolean exclusive,
        // boolean autoDelete,
        //@Nullable Map<String, Object> arguments
        Map<String, Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange",MqConst.ORDER_EVENT_EXCHANGE);
        params.put("x-dead-letter-routing-key",MqConst.RK_ORDER_TIMEOUT);
        params.put("x-message-ttl",60000*30);
        return new Queue(MqConst.ORDER_DELAY_QUEUE,true,false,false,params);
    }

    //绑定交换机跟延迟队列
    @Bean
    public Binding orderCreateBinding(){
        //String destination, 目的地
        // DestinationType destinationType,
        // String exchange,
        // String routingKey,
        //@Nullable Map<String, Object> arguments
        return new Binding(MqConst.ORDER_DELAY_QUEUE,
                Binding.DestinationType.QUEUE,
                MqConst.ORDER_EVENT_EXCHANGE,
                MqConst.RK_ORDER_CREATE,
                null);
    }

    //死信队列
    @Bean
    public Queue orderDeadQueue(){
        //String name,
        // boolean durable,
        // boolean exclusive,
        // boolean autoDelete,
        //@Nullable Map<String, Object> arguments
        return new Queue(MqConst.ORDER_DEAD_QUEUE,true,false,false);
    }

    //绑定交换机跟死信队列
    @Bean
    public Binding orderTimeoutBinding(){
        //String destination, 目的地
        // DestinationType destinationType,
        // String exchange,
        // String routingKey,
        //@Nullable Map<String, Object> arguments
        return new Binding(MqConst.ORDER_DEAD_QUEUE,
                Binding.DestinationType.QUEUE,
                MqConst.ORDER_EVENT_EXCHANGE,
                MqConst.RK_ORDER_TIMEOUT,
                null);
    }
}

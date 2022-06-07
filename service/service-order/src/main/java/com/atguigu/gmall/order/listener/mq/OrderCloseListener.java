package com.atguigu.gmall.order.listener.mq;

import com.atguigu.gmall.common.constants.MqConst;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.mqto.order.OrderCreateTo;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OrderCloseListener {

    @Autowired
    OrderInfoService orderInfoService;

    @RabbitListener(queues = MqConst.ORDER_DEAD_QUEUE)
    public void closeOrder(Message message, Channel channel) {
        OrderCreateTo create = null;
        try {
            //1.拿到过期订单消息
            byte[] body = message.getBody();
            String json = new String(body);
            create =  JSONs.strToObj(json, new TypeReference<OrderCreateTo>() {
            });

            Long userId = create.getUserId();
            System.out.println("userId = " + userId);
            Long orderId = create.getOrderId();
            System.out.println("orderId = " + orderId);

            //2.设置订单状态;原子关单
            orderInfoService.updateStatus(ProcessStatus.UNPAID, ProcessStatus.CLOSED, userId, orderId);
        } catch (Exception e) {
            log.error("订单关闭错误{}",e,create);
        }


        try {
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            log.error("mq发送消息异常{}" , create);

        }
    }
}

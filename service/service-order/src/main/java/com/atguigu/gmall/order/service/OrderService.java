package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;

public interface OrderService {
    OrderConfirmVo getOrderConfirmData();

    //生成一个交易号  [防重令牌]
    String generateTradeNo();

    // 校验防重令牌
    boolean checkToken(String token);

    //提交订单
    Long submitOrder(String tradeNo, OrderSubmitVo orderSubmitVo);

    //保存订单
    Long saveOrder(OrderSubmitVo orderSubmitVo);

    //订单创建完成后,给MQ发送消息
    void sendOrderCreateMsg(Long orderId);
}

package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【order_info_1(订单表 订单表)】的数据库操作Service
* @createDate 2022-06-05 21:42:10
*/
public interface OrderInfoService extends IService<OrderInfo> {

    //关单 修改订单状态
    void updateStatus(ProcessStatus unpaid, ProcessStatus closed, Long userId, Long orderId);


    //修改订单状态为已支付
    void updateOrderStatusToPAID(String ouTradeNo, Long userId, String name, String name1);

}

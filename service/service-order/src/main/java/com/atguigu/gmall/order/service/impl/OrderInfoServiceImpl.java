package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【order_info_1(订单表 订单表)】的数据库操作Service实现
* @createDate 2022-06-05 21:42:10
*/
@Service
@Slf4j
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements OrderInfoService{

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Override
    public void updateStatus(ProcessStatus originStatus, ProcessStatus modifyStatus, Long userId, Long orderId) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(userId);
        orderInfo.setId(orderId);

        orderInfo.setProcessStatus(modifyStatus.name());
        orderInfo.setOrderStatus(modifyStatus.getOrderStatus().name());


        try {
            orderInfoMapper.updateStatus(originStatus.name(),orderInfo);
        } catch (Exception e) {
            log.error("修改状态错误"+e);
        }
    }
}





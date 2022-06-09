package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author Administrator
* @description 针对表【order_info_1(订单表 订单表)】的数据库操作Mapper
* @createDate 2022-06-05 21:42:10
* @Entity com.atguigu.gmall.order.domain.OrderInfo1
*/
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    void updateStatus(@Param("originStatus") String originStatus, @Param("orderInfo") OrderInfo orderInfo);

    void updateOrderStatusToPAID(@Param("ouTradeNo") String ouTradeNo, @Param("userId") Long userId, @Param("processStatus") String processStatus, @Param("orderStatus") String orderStatus);
}





package com.atguigu.gmall.model.vo.order;

import com.atguigu.gmall.model.user.UserAddress;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderConfirmVo {
    //订单确认页需要的数据
    private List<CartItemForOrderVo> detailArrayList;
    private Integer totalNum;
    private BigDecimal totalAmount;
    private List<UserAddress> userAddressList;
    private String tradeNo;


}

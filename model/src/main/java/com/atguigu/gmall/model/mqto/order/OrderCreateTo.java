package com.atguigu.gmall.model.mqto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateTo {
    private Long orderId;
    private Long userId;
}
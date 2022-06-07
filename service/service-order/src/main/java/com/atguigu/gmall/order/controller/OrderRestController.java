package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order/auth")
public class OrderRestController {

    @Autowired
    OrderService orderService;

    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo") String tradeNo,@RequestBody OrderSubmitVo orderSubmitVo){
        //TODO 提交订单
        Long orderId = orderService.submitOrder(tradeNo, orderSubmitVo);

        return Result.ok(orderId.toString());
    }
}

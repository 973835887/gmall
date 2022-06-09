package com.atguigu.gmall.order.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rpc/inner/order")
public class OrderRpcController {

    @Autowired
    OrderService orderService;

    //返回订单确认页数据
    @GetMapping("/confirm")
    public Result<OrderConfirmVo> getOrderConfirmData(){

        OrderConfirmVo orderConfirmVo = orderService.getOrderConfirmData();

        return Result.ok(orderConfirmVo);
    }

    //根据订单Id获取订单信息
    @GetMapping("/info/{orderId}")
    public Result<OrderInfo> getOrderInfoByOrderId(@PathVariable Long orderId){
        OrderInfo orderInfo = orderService.getOrderInfoByOrderId(orderId);
        return Result.ok(orderInfo);
    }

    //修改订单状态为已支付
    @GetMapping("update/status/paid/{ouTradeNo}")
    public Result updateOrderStatusToPAID(@PathVariable String ouTradeNo){
        orderService.updateOrderStatusToPAID(ouTradeNo);
        return Result.ok();
    }

    //检查订单状态是否是未支付
    @GetMapping("check/status/{outTradeNo}")
    public Result checkOrderStatus(@PathVariable("outTradeNo") String outTradeNo){
        orderService.checkStatus(outTradeNo);
        return Result.ok();
    }

}

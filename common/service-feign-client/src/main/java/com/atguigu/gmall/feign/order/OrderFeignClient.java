package com.atguigu.gmall.feign.order;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/rpc/inner/order")
@FeignClient("service-order")
public interface OrderFeignClient {

    //返回订单确认页
    @GetMapping("/confirm")
    Result<Map<String,Object>> getOrderConfirmData();

    //根据订单Id获取订单信息
    @GetMapping("/info/{orderId}")
    Result<OrderInfo> getOrderInfoByOrderId(@PathVariable Long orderId);

    //修改订单状态为已支付
    @GetMapping("update/status/paid/{ouTradeNo}")
    public Result updateOrderStatusToPAID(@PathVariable String ouTradeNo);

    //检查订单状态是否是未支付
    @GetMapping("check/status/{out_trade_no}")
    Result checkOrderStatus(@PathVariable("out_trade_no") String out_trade_no);


}

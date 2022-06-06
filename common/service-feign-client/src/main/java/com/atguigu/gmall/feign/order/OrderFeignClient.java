package com.atguigu.gmall.feign.order;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/rpc/inner/order")
@FeignClient("service-order")
public interface OrderFeignClient {

    //返回订单确认页
    @GetMapping("/confirm")
    Result<Map<String,Object>> getOrderConfirmData();
}

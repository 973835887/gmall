package com.atguigui.gmall.web.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    OrderFeignClient orderFeignClient;

    //订单确认页
    @GetMapping("/trade.html")
    public String orderConfirmPage(Model model){

        Result<Map<String, Object>> data = orderFeignClient.getOrderConfirmData();
        Map<String, Object> objectMap = data.getData();
        model.addAllAttributes(objectMap);


        return "order/trade";
    }

    //订单列表页
    @GetMapping("/myOrder.html")
    public String orderListPage(){

        return "order/myOrder";
    }
}

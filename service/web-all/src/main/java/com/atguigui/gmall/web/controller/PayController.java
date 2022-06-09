package com.atguigui.gmall.web.controller;

import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.feign.pay.PayFeignClient;
import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class PayController {

    @Autowired
    OrderFeignClient orderFeignClient;




    //pay.html?orderId=741024077291978752
    @GetMapping("/pay.html")
    public String payPage(@RequestParam("orderId")Long orderId, Model model){

        OrderInfo orderInfo = orderFeignClient.getOrderInfoByOrderId(orderId).getData();

        model.addAttribute("orderInfo",orderInfo);
        return "payment/pay";
    }

    //支付成功页面
    @GetMapping("/pay/success.html")
    public String paySuccess(@RequestParam("out_trade_no") String out_trade_no){
        //调用SDK验证签名  ;;远程微服务调用支付模块验证签名
       // boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        //检查订单状态 是否是 未支付
        orderFeignClient.checkOrderStatus(out_trade_no);

        return "payment/success";
    }

}

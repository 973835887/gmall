package com.atguigu.gmall.pay.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.pay.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment/alipay")
public class AliPayController {
    // /api/payment/alipay/submit/{orderId}

    @Autowired
    AlipayService alipayService;

    @GetMapping(value = "/submit/{orderId}",produces = "text/html;charset=utf-8")
    public String submitOrder(@PathVariable("orderId") Long orderId) throws AlipayApiException {

       String result = alipayService.payPage(orderId);



        //6.这个页面提交给浏览器.浏览器自己渲染,

        return result;
    }
}

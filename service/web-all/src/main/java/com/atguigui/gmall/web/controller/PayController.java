package com.atguigui.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class PayController {

    //pay.html?orderId=741024077291978752
    @GetMapping("/pay.html")
    public String payPage(@RequestParam("orderId")Long orderId){
        return "payment/pay";
    }
}

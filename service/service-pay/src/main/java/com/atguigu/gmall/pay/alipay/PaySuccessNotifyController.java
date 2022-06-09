package com.atguigu.gmall.pay.alipay;

//支付宝成功之异步通知,给商户返回成功的参数

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConfig;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.pay.config.alipay.AlipayProperties;
import com.atguigu.gmall.pay.service.AlipayService;
import com.atguigu.gmall.pay.vo.PayNotifySuccessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaySuccessNotifyController {
    @Autowired
    AlipayService alipayService;

    @Autowired
    OrderFeignClient orderFeignClient;

    @PostMapping("/notify/success")
    public String paySuccessNotify(@RequestParam Map<String,String> params,
                                   PayNotifySuccessVo vo) throws AlipayApiException {
        System.out.println("支付宝异步通知 = " + vo);

        //调用SDK验证签名!!!
        boolean sign = alipayService.checkSign(params);
        if (sign){
            //验签通过
            System.out.println("验签通过");
            Result result = orderFeignClient.updateOrderStatusToPAID(vo.getOut_trade_no());
            if (result.isOk()){
                return "success";
            }
            return "no";
        }
        //验签不通过
        return "no";
    }

}

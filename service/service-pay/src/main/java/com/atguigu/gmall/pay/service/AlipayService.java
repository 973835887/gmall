package com.atguigu.gmall.pay.service;

import com.alipay.api.AlipayApiException;

import java.util.Map;

public interface AlipayService {
    //展示订单的收款页
    String payPage(Long orderId) throws AlipayApiException;

    //验证签名
    boolean checkSign(Map<String, String> params) throws AlipayApiException;

    //查询某次交易
    String queryTrade(String outTradeNo) throws AlipayApiException;
}

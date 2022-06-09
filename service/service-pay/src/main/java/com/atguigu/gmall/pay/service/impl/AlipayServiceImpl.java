package com.atguigu.gmall.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.pay.config.alipay.AlipayProperties;
import com.atguigu.gmall.pay.service.AlipayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AlipayServiceImpl implements AlipayService {

    @Autowired
    AlipayClient alipayClient;

    @Autowired
    AlipayProperties alipayProperties;

    @Autowired
    OrderFeignClient orderFeignClient;

    //展示订单的收款页
    @Override
    public String payPage(Long orderId) throws AlipayApiException {
        //1.创建一个阿里客户端client

        //2.准备一个支付请求
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

        //3.设置好请求参数
        alipayRequest.setReturnUrl(alipayProperties.getReturn_url());
        alipayRequest.setNotifyUrl(alipayProperties.getNotify_url());

        //根据当前订单,得到订单的价格等信息,构造出支付要用的请求参数的json
        String json =buildBizContent(orderId);

        alipayRequest.setBizContent(json);

        //4.执行请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //5.得到响应的表单页
        return result;
    }

    @Override
    public boolean checkSign(Map<String, String> params) throws AlipayApiException {
        boolean signVerified = AlipaySignature.rsaCheckV1(params,
                alipayProperties.getAlipay_public_key(),
                alipayProperties.getCharset(),
                alipayProperties.getSign_type());
        return signVerified;
    }

    //查询交易详情
    @Override
    public String queryTrade(String outTradeNo) throws AlipayApiException {
        String tradeStatus = "";
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);
        request.setBizContent(bizContent.toString());

        AlipayTradeQueryResponse response = alipayClient.execute(request);

        if (response.isSuccess()) {
            tradeStatus = response.getTradeStatus();
            return tradeStatus;
        }
        return tradeStatus;
    }


    private String buildBizContent(Long orderId) {
        Result<OrderInfo> info = orderFeignClient.getOrderInfoByOrderId(orderId);
        OrderInfo orderInfo = info.getData();


        HashMap<String, String> params = new HashMap<>();

        if (info.isOk()){
            params.put("out_trade_no",orderInfo.getOutTradeNo());
            params.put("total_amount",orderInfo.getTotalAmount().toPlainString());
            params.put("subject","尚品汇"+orderInfo.getTradeBody());
            params.put("body",orderInfo.getTradeBody());
            params.put("product_code","FAST_INSTANT_TRADE_PAY");
            //	订单绝对超时时间。
            //格式为yyyy-MM-dd HH:mm:ss。
            Date expireTime = orderInfo.getExpireTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String exTime = simpleDateFormat.format(expireTime);
            System.out.println("过期时间 = " + exTime);
            params.put("time_expire",exTime);
//            params.put("business_params",);
        }

        return JSONs.toStr(params);
    }
}

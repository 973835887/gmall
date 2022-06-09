package com.atguigu.gmall.pay.config.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AlipayProperties.class)
public class AlipayAutoConfiguration {

    @Bean
    public AlipayClient alipayClient(AlipayProperties alipyProperties){
       return new DefaultAlipayClient(alipyProperties.gatewayUrl,
               alipyProperties.app_id,
               alipyProperties.merchant_private_key,
                "json",
               alipyProperties.charset,
               alipyProperties.alipay_public_key,
               alipyProperties.sign_type);
    }
}

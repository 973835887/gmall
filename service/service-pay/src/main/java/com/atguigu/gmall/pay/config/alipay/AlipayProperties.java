package com.atguigu.gmall.pay.config.alipay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.pay.alipay")
public class AlipayProperties {

    String app_id ;

    String merchant_private_key ;

    String alipay_public_key ;

    String notify_url ;

    String return_url ;

    String sign_type ;

    String charset ;

    String gatewayUrl ;

}

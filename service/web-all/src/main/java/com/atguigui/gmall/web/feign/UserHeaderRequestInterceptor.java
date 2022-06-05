package com.atguigui.gmall.web.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Component
public class UserHeaderRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        //得到当前请求
        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String userId = request.getRequest().getHeader("UserId");
        if (userId != null){
            template.header("UserId",userId);
        }

        String userTempId = request.getRequest().getHeader("UserTempId");
        if (userTempId != null){
            template.header("UserTempId",userTempId);
        }
    }
}

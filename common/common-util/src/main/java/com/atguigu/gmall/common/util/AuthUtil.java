package com.atguigu.gmall.common.util;

import com.atguigu.gmall.model.to.UserAuthTo;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AuthUtil {
    //从当前请求得到用户信息
    public static UserAuthTo getUserAuth(){

        ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String userId = request.getRequest().getHeader("UserId");
        String userTempId = request.getRequest().getHeader("UserTempId");

        UserAuthTo userAuthTo = new UserAuthTo();
        if (!StringUtils.isEmpty(userId)){
            userAuthTo.setUserId(Long.parseLong(userId));
        }

        if (!StringUtils.isEmpty(userTempId)){
            userAuthTo.setUserTempId(userTempId);
        }
        return userAuthTo;
    }
}

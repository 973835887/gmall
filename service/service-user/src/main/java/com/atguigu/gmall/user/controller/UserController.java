package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginUserResponseVo;
import com.atguigu.gmall.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserInfoService userInfoService;

    //用户登录
    @PostMapping("/passport/login")
    public Result login(@RequestBody UserInfo userInfo, HttpServletRequest request){
        String ipAddress = IpUtil.getIpAddress(request);
        //设置IP地址
        userInfo.setIpAddr(ipAddress);

        LoginUserResponseVo responseVo = userInfoService.login(userInfo);
        if (responseVo == null){
            //登录错误
            return Result.build("", ResultCodeEnum.LOGIN_ERROR);
        }

        return Result.ok(responseVo);
    }

    //用户推出
    @GetMapping("/passport/logout")
    public Result logout(@RequestHeader("token") String token){
        userInfoService.logout(token);
        return Result.ok();
    }
}

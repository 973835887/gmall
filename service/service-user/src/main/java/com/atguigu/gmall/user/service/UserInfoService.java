package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginUserResponseVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【user_info(用户表)】的数据库操作Service
* @createDate 2022-05-31 20:27:15
*/
public interface UserInfoService extends IService<UserInfo> {

    //用户登录
    LoginUserResponseVo login(UserInfo userInfo);

    //保存用户认证信息
    String saveUserAuthInfo(UserInfo userInfo);

    //用户退出
    void logout(String token);
}

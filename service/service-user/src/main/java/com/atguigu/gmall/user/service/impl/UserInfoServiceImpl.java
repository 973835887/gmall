package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.constants.RedisConst;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginUserResponseVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* @author Administrator
* @description 针对表【user_info(用户表)】的数据库操作Service实现
* @createDate 2022-05-31 20:27:15
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public LoginUserResponseVo login(UserInfo userInfo) {

        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        query.eq("login_name",userInfo.getLoginName());


        query.eq("passwd",MD5.encrypt(userInfo.getPasswd()));
        UserInfo user = userInfoMapper.selectOne(query);

        if (user == null){
            //账号或者密码错误
            return null;
        }
        LoginUserResponseVo responseVo = new LoginUserResponseVo();

        //保存用户信息到redis中
        user.setIpAddr(userInfo.getIpAddr());
        String token = saveUserAuthInfo(user);

        responseVo.setToken(token);
        responseVo.setNickName(user.getNickName());

        return responseVo;
    }

    //
    @Override
    public String saveUserAuthInfo(UserInfo userInfo) {
        String token = UUID.randomUUID().toString().replace("-", "");
        //user:login:token 保存令牌
        redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_PREFIX+token, JSONs.toStr(userInfo),7, TimeUnit.DAYS);

        return token;
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete(RedisConst.USER_LOGIN_PREFIX+token);
    }
}





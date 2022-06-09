package caom.atguigu.gmall.gateway.filter;

import caom.atguigu.gmall.gateway.properties.AuthProperties;
import com.atguigu.gmall.common.constants.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.model.user.UserInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 处理用户认证功能
 * 1.根据用户token把用户id透传
 * 2.基本的鉴权
 */
@Slf4j
@Component
public class UserAuthFilter implements GlobalFilter {

    @Autowired
    AuthProperties properties;

    //ant风格路径匹配器
    AntPathMatcher matcher = new AntPathMatcher();

    @Autowired
    StringRedisTemplate redisTemplate;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.info("UserAuthFilter 开始拦截:请求路径{}",path);
        //1.所有人都能访问的路径直接放行 /css/** /js/** /img/**
        List<String> anyonturls = properties.getAnyonturls();
        for (String anyonturl : anyonturls) {
            boolean match = matcher.match(anyonturl, path);
            if (match){
                //1.当前path是任何人都能访问的路径
                return chain.filter(exchange);
            }
        }

        //2,任何情况都不能访问
        List<String> denyurls = properties.getDenyurls();
        for (String denyurl : denyurls) {
            boolean match = matcher.match(denyurl, path);
            if (match){
                //1.构造响应
                Result result = Result.build("", ResultCodeEnum.FORBIDDEN);
                //2.转成JSON
                String toStr = JSONs.toStr(result);
                //3 得到DataBuffer
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(toStr.getBytes(StandardCharsets.UTF_8));
                //4. 将Databuffer 发布出去
                Publisher<? extends DataBuffer> body = Mono.just(buffer);
                //4.1 防止乱码
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);

                //5.Response 其实是一个响应数据的订阅者
                return exchange.getResponse().writeWith(body);

            }

        }

        //3.说明这些请求需要登录才能访问
        List<String> authurls = properties.getAuthurls();
        for (String authurl : authurls) {
            boolean match = matcher.match(authurl, path);
            if (match){
               boolean check= validateToken(request);
               if (!check){
                   //检查失败;1.没带令牌2,带了是假的
                   //打回登录页面;重定向
                   return locationToLoginPage(exchange);
               }
            }

        }

        //4.正常请求
        String token = getToken(request);
        if (StringUtils.isEmpty(token)){
            //没带token;直接放行,看tempId是否携带
            //透传userTempId
           String userTempId = getUserTempId(request);

            ServerHttpRequest newRequest = exchange.getRequest().mutate().header("userTempId",userTempId).build();

            ServerWebExchange build = exchange.mutate().request(newRequest).response(exchange.getResponse()).build();


            return chain.filter(build);

        }else {
            boolean validate = validateToken(request);
            if (!validate){
               return locationToLoginPage(exchange);
            }else {
                ServerHttpRequest orginRequest = exchange.getRequest();

                String ipAddress = IpUtil.getGatwayIpAddress(orginRequest);
                UserInfo userInfo = getRedisTokenValue(token, ipAddress);

                //透传userTempId
                String userTempId = getUserTempId(request);

                //自己加一个请求头:原请求头不能修改

                //orginRequest.getHeaders().add("UserId",userInfo.getId().toString());
                ServerHttpRequest newRequest = exchange.getRequest().mutate().header("UserId", userInfo.getId().toString()).header("UserTempId",userTempId).build();

                ServerWebExchange build = exchange.mutate().request(newRequest).response(exchange.getResponse()).build();

                return chain.filter(build);
            }
        }


//        Mono<Void> filter = chain.filter(exchange);
//
//        return filter;

    }

    private String getUserTempId(ServerHttpRequest request) {
            //获取用户临时tempid
        //1.获取到token[Cookie:token=xx]   请求头中含有[token=xxx]
        String userTempId ="";
        HttpCookie cookie = request.getCookies().getFirst("userTempId");
        if (cookie != null){
            userTempId = cookie.getValue();
        }else {
            //token在请求头中
            userTempId = request.getHeaders().getFirst("userTempId");
        }
        return userTempId;
    }

    //打回到登录页面
    private Mono<Void> locationToLoginPage(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();

        //响应状态码
        response.setStatusCode(HttpStatus.FOUND);
        //响应头 Location 新的重定向位置
        String originUrl = request.getURI().toString();
        URI uri = URI.create(properties.getLoginPage()+"?originUrl="+originUrl);
        response.getHeaders().setLocation(uri);


        //删除之前的假令牌  立即过期 0L
        ResponseCookie cookie = ResponseCookie.from("token","12312").maxAge(0L).domain(".gmall.com").build();
        response.addCookie(cookie);

        Mono<Void> voidMono = response.setComplete();
        return voidMono;
    }

    private String getToken(ServerHttpRequest request){
        //1.获取到token[Cookie:token=xx]   请求头中含有[token=xxx]
        String token ="";
        HttpCookie cookie = request.getCookies().getFirst("token");
        if (cookie != null){
            token = cookie.getValue();
        }else {
            //token在请求头中
            token = request.getHeaders().getFirst("token");
        }
        return token;
    }

    //校验令牌token
    private boolean validateToken(ServerHttpRequest request) {
        //1.获取到token[Cookie:token=xx]   请求头中含有[token=xxx]
        String token ="";
        HttpCookie cookie = request.getCookies().getFirst("token");
        if (cookie != null){
            token = cookie.getValue();
        }else {
            //token在请求头中
             token = request.getHeaders().getFirst("token");
        }

        if (StringUtils.isEmpty(token)){
            //没有携带token
            return false;
        }else {
            //redis中有没有对应的token
            String ipAddress = IpUtil.getGatwayIpAddress(request);
            UserInfo userInfo = getRedisTokenValue(token, ipAddress);
            if (userInfo == null){
                return false;
            }
            return true;
        }
    }

    //判断IP对不对
    public UserInfo getRedisTokenValue(String token,String ipAddress){
        String result = redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_PREFIX + token);
        if (StringUtils.isEmpty(result)){
            //redis中没有数据
            return null;
        }else {
            UserInfo userInfo = JSONs.strToObj(result, new TypeReference<UserInfo>() {
            });
            if (!userInfo.getIpAddr().equals(ipAddress)){
                //ip不一致
                return null;
            }
            return userInfo;
        }
    }
}

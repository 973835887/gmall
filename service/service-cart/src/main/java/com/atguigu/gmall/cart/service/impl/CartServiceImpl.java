package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constants.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.AuthUtil;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.feign.product.ProductFeignClient;
import com.atguigu.gmall.model.cart.CartItem;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.to.UserAuthTo;
import com.fasterxml.jackson.core.type.TypeReference;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignClient productFeignClient;

    @Qualifier("otherPool")
    @Autowired
    ThreadPoolExecutor otherPool;

    @Override
    public CartItem addSkuToCart(Long skuId, Integer skuNum) {
        //决定使用哪个购物车主键
        String cartkey = determinCartkey();

        //异常机制
        validateCartOverflow(cartkey);

        //保存这个商品
        CartItem cartItem = saveSkuToCart(skuId, skuNum, cartkey);

        //给临时购物车设置过期时间
        UserAuthTo userAuth = AuthUtil.getUserAuth();
        if (userAuth.getUserId() == null){
            //用户没登录,设置过期时间
            setCartTimeout(cartkey);
        }

        return cartItem;
    }

    //设置过期时间
    @Override
    public void setCartTimeout(String cartKey) {
        //如果这个购物车已经设置了过期时间,就不用设置,没有就设置
        //3个月过期;自动延期
        redisTemplate.expire(cartKey,RedisConst.TEMP_CART_TIMMEOUT);
    }

    //设置购物车商品数量
    @Override
    public void validateCartOverflow(String cartkey) {
        Long size = redisTemplate.boundHashOps(cartkey).size();
        if (size >= RedisConst.CART_SIZE_LIMIT){
            throw new GmallException(ResultCodeEnum.CART_OVERFLOW);
        }
    }

    @Override
    public void updateCartItemPrice(String cartKey, Long skuId, BigDecimal price) {
        //拿到redis购物车中这个商品
        CartItem cartItem = getCartItem(cartKey, skuId);
        //2.修改价格
        cartItem.setSkuPrice(price);
        //3.重新存进去
        saveCartItem(cartKey,cartItem);
    }


    //决定购物车使用的键
    @Override
    public String determinCartkey() {
        String prefix = RedisConst.CART_KEY_PREFIX;
        //获取用户信息
        UserAuthTo userAuth = AuthUtil.getUserAuth();
        if (userAuth.getUserId() != null){
            //用户登录
            return prefix+userAuth.getUserId();
        } else  {
            //未登录
            return prefix+userAuth.getUserTempId();
        }
    }

    //保存商品到购物车
    @Override
    public CartItem saveSkuToCart(Long skuId, Integer skuNum, String cartKey) {
        //绑定一个指定购物车的操作
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);

        Boolean key = cart.hasKey(skuId.toString());
        if (!key){
            //如果没存过就是新增
            //1. 远程调用商品服务查一下这个商品的详细信息
            Result<SkuInfo> skuInfo = productFeignClient.getSkuInfo(skuId);

            //2. 制作一个CartItem
            CartItem cartItem = convertSkuInfoToCartItem(skuInfo.getData());
            cartItem.setSkuNum(skuNum);

            //3.并且转为json,存到redis
            String json = JSONs.toStr(cartItem);
            cart.put(skuId.toString(),json);
            return cartItem;
        }else {
            // 有就是添加数量
            String json = cart.get(skuId.toString());
            CartItem cartItem = JSONs.strToObj(json, new TypeReference<CartItem>() {
            });
            cartItem.setSkuNum(cartItem.getSkuNum()+skuNum);
            //写会redis中
            cart.put(skuId.toString(),JSONs.toStr(cartItem));

            return cartItem;
        }
    }

    //查询购物车列表
    @Override
    public List<CartItem> getCartItems() {

        BoundHashOperations<String, String, String> userCart = getUserCart();
        BoundHashOperations<String, String, String> tempCart = getTempCart();



        // 1.判断是否需要合并购物车[UserId UserTempId]
        if (userCart != null && tempCart != null && tempCart.size() >0){
            //判断 ,超出就不用合并
            if ((userCart.size() + tempCart.size())>=RedisConst.CART_SIZE_LIMIT){
                throw new GmallException(ResultCodeEnum.CART_MERGE_OVERFLOW);
            }
            // 3.如果需要合并[登录了.用户购物车跟临时购物车都有东西],
            // 拿到临时购物车中的所有商品数据
            String tempCartKey = getTempCartKey();
            List<CartItem> tempItems = getItemLists(tempCartKey);

            String userCartKey = getUserCartKey();
            //遍历临时购物车的数据挨个添加到用户购物车
            tempItems.stream().forEach(cartItem -> {
                saveSkuToCart(cartItem.getSkuId(), cartItem.getSkuNum(), userCartKey);
            });

            // 4.合并操作,把临时购物车中的商品移到用户购物车.并把临时购物车中的数据删除
            deleteCart(tempCartKey);

            // 5.返回合并后的数据
            List<CartItem> itemLists = getItemLists(userCartKey);


            //提交给线程池 8
            updatePriceBatch(userCartKey);

            
            return itemLists;
        }else {
            //无需合并
            // 2.如果不需要合并[未登录UserTempId或者登录了][但是临时购物车是空的][登录了,但是临时购物车被合并过了]
            String cartkey= determinCartkey();

            List<CartItem> cartItems = getItemLists(cartkey);

            updatePriceBatch(cartkey);

            return cartItems;
        }
    }

    public void updatePriceBatch(String cartkey) {
        //提交给线程池 - 8
        otherPool.submit(()->{
            List<CartItem> items = getItemLists(cartkey);
            //6.更新价格
            items.stream().forEach(cartItem -> {
                //1.查价
                Result<BigDecimal> skuPrice = productFeignClient.getSkuPrice(cartItem.getSkuId());
                //2.改redis的价格
                updateCartItemPrice(cartkey,cartItem.getSkuId(),skuPrice.getData());
            });
        },otherPool);
    }

    //获取所有选中的商品列表
    @Override
    public List<CartItem> getCheckItem() {
        //1.获取购物车中的所有商品列表
        List<CartItem> cartItems = getCartItems();

        //2.过滤选中的
        List<CartItem> cartItemList = cartItems.stream()
                .filter((item) -> item.getIsChecked() == 1)
                .collect(Collectors.toList());

        return cartItemList;
    }


    private String getUserCartKey(){
        UserAuthTo userAuth = AuthUtil.getUserAuth();
        Long userId = userAuth.getUserId();
        if (userId != null){
            String cartKey = RedisConst.CART_KEY_PREFIX+userId;
            return cartKey;
        }
        return null;
    }

    private String getTempCartKey(){
        UserAuthTo userAuth = AuthUtil.getUserAuth();
        String tempId = userAuth.getUserTempId();
        if (!StringUtils.isEmpty(tempId)){
            String cartKey = RedisConst.CART_KEY_PREFIX+tempId;
            return cartKey;
        }
        return null;
    }


    private BoundHashOperations<String, String, String> getUserCart(){
        UserAuthTo userAuth = AuthUtil.getUserAuth();
        Long userId = userAuth.getUserId();
        if (userId != null){
            String cartKey = RedisConst.CART_KEY_PREFIX+userId;
            return redisTemplate.boundHashOps(cartKey);
        }
        return null;
    }

    private BoundHashOperations<String, String, String> getTempCart(){
        UserAuthTo userAuth = AuthUtil.getUserAuth();
        String tempId = userAuth.getUserTempId();
        if (!StringUtils.isEmpty(tempId)){
            String cartKey = RedisConst.CART_KEY_PREFIX+tempId;
            return redisTemplate.boundHashOps(cartKey);
        }
        return null;
    }

    //修改购物车数量 增减一
    @Override
    public void updateCartItemNum(Long skuId, Integer skuNum) {
        //1.拿到购物车的键
        String cartkey = determinCartkey();
        //2.拿到购物车
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartkey);
        
        //3.拿到redis中对应购物车的value
       CartItem cartItem = getCartItem(cartkey,skuId);

       //3.1  判断此时购物车的数量是否为1

       //4.修改数量
        if (skuNum.equals(1) ||skuNum.equals(-1)){
            cartItem.setSkuNum(cartItem.getSkuNum()+skuNum);
        }else {
            cartItem.setSkuNum(skuNum);
        }
        cartItem.setUpdateTime(new Date());

        //修改远程的数据
        String json = JSONs.toStr(cartItem);
        cart.put(skuId.toString(),json);
    }

    //修改购物车的选中数量
    @Override
    public void updateCartItemStatus(Long skuId, Integer status) {
        String cartkey = determinCartkey();
        CartItem cartItem = getCartItem(cartkey, skuId);
        //设置状态跟更新时间
        cartItem.setIsChecked(status);
        cartItem.setUpdateTime(new Date());
        //写回redis
        saveCartItem(cartkey,cartItem);

    }

    //删除指定购物车中的商品
    @Override
    public void deleteCartItem(Long skuId) {
        String cartkey = determinCartkey();

        deleteItem(cartkey,skuId);
    }

    //删除购物车中选中的商品
    @Override
    public void deleteChecked() {
        String cartkey = determinCartkey();
        //1.拿到购物车中所有的商品
        List<CartItem> cartItems = getCartItems();

        //2.只要选中的商品,然后删除;可变参数是数组
        Object[] objects = cartItems.stream()
                .filter((item) -> item.getIsChecked() == 1)
                .map((item) -> item.getSkuId().toString())
                .toArray();

        if (objects!=null&&objects.length>0){
            try {
                deleteCheckeds(cartkey,objects);
                log.info("商品删除完成");
            } catch (Exception e) {
                log.error("删除失败"+e);
            }
        }

    }


    //删除整个购物车
    @Override
    public void deleteCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }


    //删除选中购物车中的商品 中得
    private void deleteCheckeds(String cartkey,Object[] skuIds) {
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartkey);
        cart.delete(skuIds);
    }


    //删除指定购物车中的商品 中得
    private void deleteItem(String cartkey,Long skuId) {
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartkey);
        cart.delete(skuId.toString());
    }

    //给redis中存数据; 新增 覆盖修改
    private void saveCartItem(String cartkey,CartItem cartItem) {
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartkey);

        cart.put(cartItem.getSkuId().toString(),JSONs.toStr(cartItem));
    }

    //拿到购物车中的一个商品
    private CartItem getCartItem(String cartkey, Long skuId) {
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartkey);
        String json = cart.get(skuId.toString());
        CartItem cartItem = JSONs.strToObj(json, new TypeReference<CartItem>() {});
        return cartItem;
    }

    //获取购物车所有数据
    private List<CartItem> getItemLists(String cartkey) {
        //1.拿到购物车
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartkey);

        //2.获取所有的商品
        List<String> values = cart.values();

        //3.List<String>转成List<CartItem>
        List<CartItem> cartItems = values.stream()
                .map((jsonStr) -> {
                     CartItem cartItem = JSONs.strToObj(jsonStr, new TypeReference<CartItem>() {
                     });
                    return cartItem;
        }).sorted((o1,o2)-> o2.getUpdateTime().compareTo(o1.getUpdateTime()))
        .collect(Collectors.toList());

        return cartItems;
//        List<CartItem> cartItems = new ArrayList<>();
//        for (String json : values) {
//            CartItem cartItem = JSONs.strToObj(json, new TypeReference<CartItem>() {
//            });
//            cartItems.add(cartItem);
//        }
    }

    //保存cartItem数据
    private CartItem convertSkuInfoToCartItem(SkuInfo data) {
        UserAuthTo userAuth = AuthUtil.getUserAuth();

        CartItem cartItem = new CartItem();
        cartItem.setId(data.getId());

        if (userAuth.getUserId()!=null){
            cartItem.setUserId(userAuth.getUserId().toString());
        }else {
            cartItem.setUserId(userAuth.getUserTempId());
        }


        cartItem.setSkuId(data.getId());

        cartItem.setSkuNum(0);

        cartItem.setSkuDefaultImg(data.getSkuDefaultImg());
        cartItem.setSkuName(data.getSkuName());

        cartItem.setIsChecked(1);
        cartItem.setCreateTime(new Date());
        cartItem.setUpdateTime(new Date());

        cartItem.setCartPrice(data.getPrice());
        cartItem.setSkuPrice(data.getPrice());
        return cartItem;
    }
}

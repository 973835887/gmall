package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.common.constants.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.AuthUtil;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.ProductFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.model.cart.CartItem;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.PaymentWay;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.to.UserAuthTo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.order.CartItemForOrderVo;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    WareFeignClient wareFeignClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    OrderDetailService orderDetailService;

    //订单提交页面详情
    @Override
    public OrderConfirmVo getOrderConfirmData() {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
//        orderConfirmVo.setDetailArrayList();
//        orderConfirmVo.setTotalNum();
//        orderConfirmVo.setTotalAmount();
//        orderConfirmVo.setUserAddressList();
//        orderConfirmVo.setTradeNo();
        Result<List<CartItem>> checkItem = cartFeignClient.getCheckItem();
        if (checkItem.isOk()) {
            List<CartItem> items = checkItem.getData();

            List<CartItemForOrderVo> vos = items.stream()
                    .map(cartItem -> {
                        CartItemForOrderVo vo = new CartItemForOrderVo();
                        vo.setImgUrl(cartItem.getSkuDefaultImg());
                        vo.setSkuName(cartItem.getSkuName());
                        BigDecimal skuPrice = productFeignClient.getSkuPrice(cartItem.getSkuId()).getData();
                        vo.setSkuNum(cartItem.getSkuNum());
                        vo.setOrderPrice(skuPrice);

                        //库存系统实时查询库存
                        String stock = wareFeignClient.hasStock(cartItem.getSkuId(), cartItem.getSkuNum());
                        vo.setStock(stock);

                        return vo;
                    }).collect(Collectors.toList());

            //设置所有选中的商品
            orderConfirmVo.setDetailArrayList(vos);

            //计算总量
            Integer totalNum = items.stream().map(CartItem::getSkuNum)
                    .reduce((a, b) -> a + b).get();
            orderConfirmVo.setTotalNum(totalNum);


            //计算价格
            BigDecimal totalPrice = vos.stream().map(item -> item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum().toString())))
                    .reduce((a, b) -> a.add(b))
                    .get();
            orderConfirmVo.setTotalAmount(totalPrice);
        }

        List<UserAddress> data = userFeignClient.getUserAddressList().getData();
        //设置用户地址列表
        orderConfirmVo.setUserAddressList(data);

        //设置tradeNo
        String tradeNo = generateTradeNo();
        orderConfirmVo.setTradeNo(tradeNo);



        return orderConfirmVo;
    }

    //生成订单交易号
    @Override
    public String generateTradeNo() {
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(RedisConst.NO_REPEAT_TOKEN+token,"1",10, TimeUnit.MINUTES);
        return token;
    }

    //校验令牌
    @Override
    public boolean checkToken(String token) {
        //原子验删令牌
        String script = "if redis.call('get', KEYS[1]) == '1' then return redis.call('del', KEYS[1]) else return 0 end";

        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(RedisConst.NO_REPEAT_TOKEN + token), "1");

        return result == 1L;
    }

    //提交订单
    @Override
    public Long submitOrder(String tradeNo, OrderSubmitVo orderSubmitVo) {
        //1.验证令牌
        boolean token = checkToken(tradeNo);
        if (!token){
            throw new GmallException(ResultCodeEnum.REQ_ILLEGAL_TOKEN_ERROR);
        }

        //2.验价格:总价
        //2.1前端提交的总价
        BigDecimal frontTotalPrice = orderSubmitVo.getOrderDetailList().stream()
                .map(item -> item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum().toString())))
                .reduce((a, b) -> a.add(b))
                .get();

        //2.2购物车中选中这个商品的总价
        Result<List<CartItem>> checkItems = cartFeignClient.getCheckItem();
        BigDecimal backTotalPrice = checkItems.getData().stream().map(cartItem -> {
                    Result<BigDecimal> skuPrice = productFeignClient.getSkuPrice(cartItem.getSkuId());
                    BigDecimal price = skuPrice.getData();
                    Integer skuNum = cartItem.getSkuNum();
                    return price.multiply(new BigDecimal(skuNum.toString()));
                })
                .reduce((a, b) -> a.add(b)).get();

        //2.3比对
        if (frontTotalPrice.compareTo(backTotalPrice) != 0){
            throw new GmallException(ResultCodeEnum.ORDER_PRICE_CHANGE);
        }

        //3.验库存
        List<String> noStock = new ArrayList<>();
        checkItems.getData().stream()
                .forEach(item -> {
                    String stock = wareFeignClient.hasStock(item.getSkuId(), item.getSkuNum());
                    if (!"1".equals(stock)){
                        //没库存
                        noStock.add("["+item.getSkuName()+":没有库存]");
                    }
                });
        if (noStock.size() >0){
            String msg = noStock.stream()
                    .reduce((a, b) -> a +","+ b)
                    .get();

            GmallException exception = new GmallException(msg,ResultCodeEnum.PRODUCT_NO_STOCK.getCode());

            throw exception;
        }

        //4.保存订单
        Long orderId = saveOrder(orderSubmitVo);


        return orderId;
    }


    //保存订单
    @Transactional
    @Override
    public Long saveOrder(OrderSubmitVo orderSubmitVo) {

        //绑定表 一个order_info_x 对应一个order_detail_x表
        //保存订单
        OrderInfo orderInfo =  prepareOrderInfo(orderSubmitVo);
        orderInfoService.save(orderInfo);


        //保存订单项
       List<OrderDetail> orderDetails = prepareOrderDetail(orderInfo);
        orderDetailService.saveBatch(orderDetails);
        return orderInfo.getId();
    }

    //准备订单项的数据
    private List<OrderDetail> prepareOrderDetail(OrderInfo orderInfo) {
        //1.拿到订单需要购买的所有商品
        List<CartItem> items = cartFeignClient.getCheckItem().getData();

        //2.每个要购买的商品都是订单项数据
        List<OrderDetail> detailList = items.stream()
                .map(item -> {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrderId(orderInfo.getId());
                    Long userId = AuthUtil.getUserAuth().getUserId();

                    detail.setUserId(userId);
                    detail.setSkuId(item.getSkuId());
                    detail.setSkuName(item.getSkuName());
                    detail.setImgUrl(item.getSkuDefaultImg());
                    detail.setOrderPrice(item.getSkuPrice());
                    detail.setSkuNum(item.getSkuNum());
                    detail.setHasStock("1");
                    detail.setCreateTime(new Date());
                    detail.setSplitTotalAmount(new BigDecimal(0));
                    detail.setSplitActivityAmount(new BigDecimal(0));
                    detail.setSplitCouponAmount(new BigDecimal(0));


                    return detail;
        }).collect(Collectors.toList());
        return detailList;
    }


    //准备orderinfo数据
    private OrderInfo prepareOrderInfo(OrderSubmitVo vo) {
        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setConsignee(vo.getConsignee());
        orderInfo.setConsigneeTel(vo.getConsigneeTel());

        List<CartItemForOrderVo> detailList = vo.getOrderDetailList();
        BigDecimal totalAmount = detailList.stream()
                .map(item -> item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum().toString())))
                .reduce((a, b) -> a.add(b)).get();


        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());

        UserAuthTo auth = AuthUtil.getUserAuth();

        orderInfo.setUserId(auth.getUserId());
        orderInfo.setPaymentWay(PaymentWay.ONLINE.name());

        orderInfo.setDeliveryAddress(vo.getDeliveryAddress());
        orderInfo.setOrderComment(vo.getOrderComment());

        String substring = UUID.randomUUID().toString().substring(0, 5);
        orderInfo.setOutTradeNo("GMALL-"+System.currentTimeMillis()+"-"+auth.getUserId()+"-"+substring);

        //所有的购买商品名称
        String skuNames = detailList.stream().map(CartItemForOrderVo::getSkuName)
                .reduce((a, b) -> a +"|"+ b).get();
        orderInfo.setTradeBody(skuNames);

        //过期时间
        orderInfo.setCreateTime(new Date());
        long time = System.currentTimeMillis() + (1000 * 60 * 30);
        orderInfo.setExpireTime(new Date(time));


        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());

        orderInfo.setTrackingNo("");

        orderInfo.setParentOrderId(0L);

        //订单图片
        orderInfo.setImgUrl(detailList.get(0).getImgUrl());

//        orderInfo.setOrderDetailList(vo.getOrderDetailList());

        orderInfo.setWareId("");
        orderInfo.setProvinceId(0L);

        orderInfo.setActivityReduceAmount(new BigDecimal(0));
        orderInfo.setCouponAmount(new BigDecimal(0));
        orderInfo.setOriginalTotalAmount(new BigDecimal(0));
        //可退款日期
        orderInfo.setRefundableTime(null);
        orderInfo.setFeightFee(new BigDecimal(0));
        orderInfo.setOperateTime(new Date());
//        orderInfo.setId();
        return orderInfo;
    }
}

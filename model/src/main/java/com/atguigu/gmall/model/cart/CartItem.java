package com.atguigu.gmall.model.cart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(description = "购物车")
public class CartItem  {
    private static final long serialVersionUID = 1L;

    private Long id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "skuid")
    private Long skuId;

    @ApiModelProperty(value = "放入购物车时价格")
    private BigDecimal cartPrice;

    @ApiModelProperty(value = "数量")
    private Integer skuNum;

    @ApiModelProperty(value = "图片文件")
    private String skuDefaultImg;

    @ApiModelProperty(value = "sku名称 (冗余)")
    private String skuName;

    @ApiModelProperty(value = "isChecked")
    private Integer isChecked = 1;

    //  ,fill = FieldFill.INSERT
    private Date createTime;

    //  ,fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    // 实时价格 skuInfo.price
    BigDecimal skuPrice;

//    //  优惠券信息列表
//    @ApiModelProperty(value = "购物项对应的优惠券信息")
//    private List<CouponInfo> couponInfoList;

}

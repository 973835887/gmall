package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author Administrator
* @description 针对表【sku_sale_attr_value(sku销售属性值)】的数据库操作Service
* @createDate 2022-05-20 15:42:58
*/
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValue> {

    Map<String, String> getSkuValueJson(Long skuId);
}

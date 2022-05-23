package com.atguigu.gmall.model.to;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class SkuDetailTo {
    BaseCategoryView categoryView;
    SkuInfo skuInfo;
    BigDecimal price;
    List<SpuSaleAttr> spuSaleAttrList;
    String valuesSkuJson;
}



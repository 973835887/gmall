package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.dto.ValueJsonDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【sku_sale_attr_value(sku销售属性值)】的数据库操作Mapper
* @createDate 2022-05-20 15:42:58
* @Entity com.atguigu.gmall.product.domain.SkuSaleAttrValue
*/
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    List<ValueJsonDto> getSkuValueJson(@Param("skuId") Long skuId);
}





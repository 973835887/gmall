package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_attr_info(属性表)】的数据库操作Mapper
* @createDate 2022-05-18 16:24:54
* @Entity com.atguigu.gmall.product.domain.BaseAttrInfo
*/
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    List<BaseAttrInfo> getAttrInfoList(@Param("categoryId1") Long categoryId1, @Param("categoryId2") Long categoryId2, @Param("categoryId3") Long categoryId3);


    //根据skuId查询出对应的所有平台属性名和值.为了检索
    List<SearchAttr> getSkuBaseAttrNameAndValue(@Param("skuId") Long skuId);
}





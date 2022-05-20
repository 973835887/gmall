package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_sale_attr(基本销售属性表)】的数据库操作Service
* @createDate 2022-05-19 15:35:44
*/
public interface BaseSaleAttrService extends IService<BaseSaleAttr> {

    List<BaseSaleAttr> getBaseSaleAttrList();
}

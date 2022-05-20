package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.mapper.BaseSaleAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_sale_attr(基本销售属性表)】的数据库操作Service实现
* @createDate 2022-05-19 15:35:44
*/
@Service
public class BaseSaleAttrServiceImpl extends ServiceImpl<BaseSaleAttrMapper, BaseSaleAttr>
    implements BaseSaleAttrService{

    @Autowired
    BaseSaleAttrMapper baseSaleAttrMapper;

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrs = baseSaleAttrMapper.selectList(null);
        return baseSaleAttrs;
    }
}





package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

/**
* @author Administrator
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2022-05-20 15:42:58
*/
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Qualifier("skuIdBloom")
    @Autowired
    RBloomFilter<Object> skuIdBloom;

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
        Long skuInfoId = skuInfo.getId();

        skuIdBloom.add(skuInfoId);


        System.out.println("skuInfoId = " + skuInfoId);
        Long spuId = skuInfo.getSpuId();

        //保存Sku平台属性值
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuInfoId);
            skuAttrValueMapper.insert(skuAttrValue);
        }


        //保存Sku销售属性值
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuInfoId);
            skuSaleAttrValue.setSpuId(spuId);
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }

        //保存库存单元图片列表
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuInfoId);
            skuImageMapper.insert(skuImage);
        }

    }

    //下架
    @Override
    public void updateStatus(Long skuId, int status) {
        //保存 is_sale 状态
        skuInfoMapper.updateStatus(skuId,status);
        //TODO 给ES 保存/删除
    }

    //查询商品价格
    @Override
    public BigDecimal getSkuPrice(Long skuId) {

        return skuInfoMapper.getSkuPrice(skuId);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrAndValueBySkuId(Long skuId) {
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.getSpuSaleAttrAndValueBySkuId(skuId);
        return spuSaleAttrs;
    }

    @Override
    public List<Long> getSkuIds() {
        List<Long>  ids = skuInfoMapper.getSkuIds();
        return ids;
    }


}





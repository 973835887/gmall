package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【spu_info(商品表)】的数据库操作Service
* @createDate 2022-05-19 14:01:25
*/
public interface SpuInfoService extends IService<SpuInfo> {

    Page<SpuInfo> getPageInfo(Long pageNum, Long pageSize, Long category3Id);


    void savespuInfo(SpuInfo spuInfo);
}

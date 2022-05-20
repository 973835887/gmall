package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_image(商品图片表)】的数据库操作Service
* @createDate 2022-05-19 16:30:11
*/
public interface SpuImageService extends IService<SpuImage> {

    List<SpuImage> getSpuImageList(Long spuId);
}

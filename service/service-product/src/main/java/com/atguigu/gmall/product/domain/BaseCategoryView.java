package com.atguigu.gmall.product.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName base_category_view
 */
@TableName(value ="base_category_view")
@Data
public class BaseCategoryView implements Serializable {
    /**
     * 编号
     */
    private Long id;

    /**
     * 编号
     */
    private Long category1Id;

    /**
     * 分类名称
     */
    private String category1Name;

    /**
     * 编号
     */
    private Long category2Id;

    /**
     * 二级分类名称
     */
    private String category2Name;

    /**
     * 编号
     */
    private Long category3Id;

    /**
     * 三级分类名称
     */
    private String category3Name;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
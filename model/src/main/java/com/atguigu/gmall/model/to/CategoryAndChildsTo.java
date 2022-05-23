package com.atguigu.gmall.model.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAndChildsTo {
    Long categoryId;
    String categoryName;
    List<CategoryAndChildsTo> categoryChild;
}

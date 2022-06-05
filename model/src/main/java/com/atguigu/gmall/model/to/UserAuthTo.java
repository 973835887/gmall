package com.atguigu.gmall.model.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthTo {
    private Long userId;
    private String userTempId;
}

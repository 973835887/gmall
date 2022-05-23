package com.atguigu.gmall.common.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JSONs {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toStr(Object obj)  {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转字符串的JSON转字符串异常:{}"+obj);
            e.printStackTrace();
        }
        return null;
    }
}
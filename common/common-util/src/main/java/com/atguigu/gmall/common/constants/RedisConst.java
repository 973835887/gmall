package com.atguigu.gmall.common.constants;

import java.time.Duration;

public class RedisConst {
    public static final String CATEGORY_KEY = "catregorys";


    public static final String SKU_CACHE_KEY_PREFIX = "sku:detail";

    public static final String BLOOM_SKUID_KEY = "bloom:skuid";

    public static final String LOCK_PREFIX = "lock";
    public static final String SKUDETAIL_LOCK_PREFIX = "lock:detail";
    public static final String SALE_ATTR_CACHE_KEY = "sale:attr";

    public static final String SKU_HOTSCORE = "sku:hotscore";
    public static final String USER_LOGIN_PREFIX = "user:login:";

    public static final String CART_KEY_PREFIX = "user:cart:";

    public static final Duration TEMP_CART_TIMMEOUT = Duration.ofDays(90);
    public static final Long CART_SIZE_LIMIT = 200L;
    public static final String NO_REPEAT_TOKEN = "norepeat:token:";
}

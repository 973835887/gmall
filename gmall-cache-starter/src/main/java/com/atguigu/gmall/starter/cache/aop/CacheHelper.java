package com.atguigu.gmall.starter.cache.aop;

import com.atguigu.gmall.starter.cache.aop.annotation.Cache;
import com.atguigu.gmall.starter.cache.service.CacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

@Component
public class CacheHelper {

    SpelExpressionParser parser = new SpelExpressionParser();

    @Autowired
    CacheService cacheService;

    @Autowired
    @Qualifier("skuIdBloom")
    RBloomFilter<Object> skuIdBloom;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    Map<String,RBloomFilter<Object>> bloomMap;

    //查询缓存
    public Object getCacheData(String cacheKey, ProceedingJoinPoint joinPoint) {
        //拿到方法的返回值类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取目标方法
        Method method = signature.getMethod();
        //带泛型的返回值类型
        Type type = method.getGenericReturnType();

        Object cacheData = cacheService.getCacheData(cacheKey, new TypeReference<Object>() {
            @Override
            public Type getType() {
                return type;
            }
        });
        return cacheData;
    }

        //判断指定的布隆过滤器中是否有指定的值
        public boolean bloomTest(String bloomName, ProceedingJoinPoint joinPoint) {
            Cache cache = getMethodCacheAnnotation(joinPoint);

            String bloomValueExp = cache.bloomValue();
            //计算布隆过滤器需要判定的值
            Object value = getExpressionValueString(joinPoint, bloomValueExp, Object.class);
            //动态取出指定布隆
            RBloomFilter<Object> bloomFilter = bloomMap.get(bloomName);


            return bloomFilter.contains(value);
        }
    //    //询问bloom
//    public boolean bloomTest(Object arg, ProceedingJoinPoint joinPoint) {
//        boolean contains = skuIdBloom.contains(arg);
//        return contains;
//    }

    //尝试加锁
    public boolean trtLock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean trylock =   lock.tryLock();
        return trylock  ;
    }

    //缓存中保存
    public void saveCacheData(String cacheKey, Object result) {
        cacheService.save(cacheKey,result);
    }

    //解锁
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.isLocked())
            lock.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String evaluteExpression(ProceedingJoinPoint joinPoint) {
        Cache cache = getMethodCacheAnnotation(joinPoint);
        //3.拿到表达式
        String cacheKeyExpression = StringUtils.isEmpty(cache.cacheKey())?cache.value():cache.cacheKey();

        String value = getExpressionValueString(joinPoint, cacheKeyExpression,String.class);

        return value;
    }

    //计算表达式--------抽取
    private <T>  T getExpressionValueString(ProceedingJoinPoint joinPoint, String cacheKeyExpression,Class<T> clz) {
        //4.计算表达式
        Expression expression = parser.parseExpression(cacheKeyExpression, ParserContext.TEMPLATE_EXPRESSION);
        //准备上下文
        StandardEvaluationContext context = new StandardEvaluationContext( );
        context.setVariable("args", joinPoint.getArgs());

        T value = expression.getValue(context, clz);
        return value;
    }

    //获取方法上的cache注解-------抽取
    private Cache getMethodCacheAnnotation(ProceedingJoinPoint joinPoint) {
        //1.拿到目标方法上标注的cache注解的cachekey的值
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //2.拿到目标方法上的注解
        Cache cache = AnnotationUtils.findAnnotation(method, Cache.class);
        return cache;
    }


    //判断是否需要启用布隆
    public String determinBloom(ProceedingJoinPoint joinPoint) {
        Cache cache = getMethodCacheAnnotation(joinPoint);
        String bloomName = cache.bloomName();
        return bloomName;
    }


}

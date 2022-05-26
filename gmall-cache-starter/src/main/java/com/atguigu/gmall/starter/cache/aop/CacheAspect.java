package com.atguigu.gmall.starter.cache.aop;

import com.atguigu.gmall.starter.constants.RedisConst;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * 自定义切面
 * 1.导入spring-boot-starter-aop
 * 2.@EnableAspectJAutoProxy
 * 3.编写切面
 * 4.切入点表达式
 */

@Aspect
@Component
public class CacheAspect {

    //10
    @Autowired
    CacheHelper cacheHelper;


    @Around(value = "@annotation(com.atguigu.gmall.starter.cache.aop.annotation.Cache)")
    public Object cacheServicearound(ProceedingJoinPoint joinPoint){
        //获取目标方法参数
        Object[] args = joinPoint.getArgs();


        Object result = null;
        try {
            //前置通知
            //动态计算表达式
            String cacheKey = cacheHelper.evaluteExpression(joinPoint);
            //1.查询缓存中有没有数据
            Object obj = cacheHelper.getCacheData(cacheKey,joinPoint);
            if (obj == null){
                String lockKey = RedisConst.LOCK_PREFIX+cacheKey;
                //10.要不要用布隆
               String bloomName = cacheHelper.determinBloom(joinPoint);
               if (StringUtils.isEmpty(bloomName)){
                   //不启用布隆,直接调用目标方法且加锁
                   //5.回源之前加锁

                   boolean lock = cacheHelper.trtLock(lockKey);
                   if(lock){
                       //6.利用反射执行目标方法
                       result = joinPoint.proceed(args);
                       //7.缓存中也保存一份
                       cacheHelper.saveCacheData(cacheKey,result);
                       //9 .解锁
                       cacheHelper.unlock(lockKey);
                       return result;

                   }//8.加锁失败,等待一秒后再次查询缓存,返回缓存中的数据
                   Thread.sleep(1000);
                   obj = cacheHelper.getCacheData(cacheKey, joinPoint);
                   return obj;
               }else {
                   //  启用布隆
                   //2,缓存中没有,询问布隆中有没有
                   boolean bloomContains =  cacheHelper.bloomTest(bloomName,joinPoint);
                   //3,布隆说有,可以回源查库
                   if (bloomContains){
                       //5.回源之前加锁

                       boolean lock = cacheHelper.trtLock(lockKey);
                       if(lock){
                           //6.利用反射执行目标方法
                           result = joinPoint.proceed(args);
                           //7.缓存中也保存一份
                           cacheHelper.saveCacheData(cacheKey,result);
                           //9 .解锁
                           cacheHelper.unlock(lockKey);
                           return result;

                       }//8.加锁失败,等待一秒后再次查询缓存,返回缓存中的数据
                       Thread.sleep(1000);
                       obj = cacheHelper.getCacheData(cacheKey, joinPoint);
                       return obj;
                   } else {
                       //4.布隆说没有,打回
                       return null;
                   }
               }




            }
            //3.缓存中有直接返回
            return  obj;
            //返回通知
        } catch (Throwable e) {
            //异常通知
            e.printStackTrace();
        }finally {
            //环绕通知
        }
        return result;
    }
}

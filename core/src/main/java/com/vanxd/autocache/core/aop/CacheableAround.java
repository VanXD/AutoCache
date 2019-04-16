package com.vanxd.autocache.core.aop;

import com.alibaba.fastjson.JSONObject;
import com.vanxd.autocache.core.annotation.Cacheable;
import com.vanxd.autocache.core.cache.CacheAfterHandle;
import com.vanxd.autocache.core.util.KeyGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * AOP对数据参数进行校验
 * 对service下的所有方法进行拦截,如果参数被加上了注解@Validate,则进使用类中的方法进行数据校验
 *
 * @author wyd
 */
@Aspect
@Component
public class CacheableAround {
    private final static Logger logger = LoggerFactory.getLogger(CacheableAround.class);
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Around("@annotation(com.vanxd.autocache.core.annotation.Cacheable)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        if (StringUtils.isEmpty(cacheable.table()) && cacheable.tables().length == 0) {
            throw new RuntimeException("注解没有设置表名, 至少设置table或者tables");
        }
        String key = KeyGenerator.generateKey(joinPoint.getArgs(), method, cacheable);
        Class returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType();
        Object proceedResult;
        String cacheResult = getCacheResult(key);
        if (cacheable.isCachePut() || StringUtils.isEmpty(cacheResult)) {
            proceedResult = joinPoint.proceed();
            CacheAfterHandle cacheAfter = new CacheAfterHandle(key, cacheable, proceedResult, joinPoint, redisTemplate);
            cacheAfter.after();
            return proceedResult;
        } else {
            logger.debug("读取缓存: {} -> {}", key, cacheResult);
            return JSONObject.parseObject(cacheResult, returnType);
        }
    }

    /**
     * 获得key在redis的缓存结果
     * @param key
     * @return
     */
    private String getCacheResult(String key) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.get(key);
    }
}
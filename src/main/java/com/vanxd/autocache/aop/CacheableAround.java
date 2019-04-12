package com.vanxd.autocache.aop;

import com.alibaba.fastjson.JSONObject;
import com.vanxd.autocache.annotation.Cacheable;
import com.vanxd.autocache.util.KeyGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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

    @Around("@annotation(com.vanxd.autocache.annotation.Cacheable)")
    public Object validtor(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        if (StringUtils.isEmpty(cacheable.table()) && cacheable.tables().length == 0) {
            throw new RuntimeException("注解没有设置表名, 至少设置table或者tables");
        }
        Object[] args = joinPoint.getArgs();
        String key = KeyGenerator.generateKey(joinPoint.getArgs(), method, cacheable);
        Class returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType();
        Object proceedResult;
        String cacheResult = getCacheResult(key);
        if (cacheable.isCachePut() || StringUtils.isEmpty(cacheResult)) {
            proceedResult = joinPoint.proceed();
            after(key, cacheable, proceedResult);
            return proceedResult;
        } else {
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

    private void after(String key, Cacheable cacheable, Object result) {
        if (cacheable.isCachePut()) {

        } else {
            logger.debug("auto cache key: {}", key);
            String jsonResult = JSONObject.toJSONString(result);
            saveKey(cacheable, key, jsonResult);
            saveKeyList(cacheable, key, jsonResult);
        }
    }

    /**
     * 把key保存到表keys中
     *
     * @param cacheable
     * @param key
     * @param jsonResult
     */
    private void saveKeyList(Cacheable cacheable, String key, String jsonResult) {
        ZSetOperations<String, String> ops = redisTemplate.opsForZSet();
        int tableCount = cacheable.tables().length;
        Arrays.stream(cacheable.tables())
                .forEach(table -> {
                    String tableKey = table + "~keys";
                    // 分值是表的数量
                    ops.add(tableKey, key, tableCount);
                });
    }

    /**
     * 保存单独的key
     *
     * @param cacheable
     * @param key
     * @param jsonResult
     */
    private void saveKey(Cacheable cacheable, String key, String jsonResult) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, jsonResult, cacheable.expireSecond(), TimeUnit.SECONDS);
    }
}
package com.vanxd.autocache.aop;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vanxd.autocache.annotation.Cacheable;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * AOP对数据参数进行校验
 * 对service下的所有方法进行拦截,如果参数被加上了注解@Validate,则进使用类中的方法进行数据校验
 * @author wyd
 */
@Aspect
@Component
public class CacheAfterReturning {
    Logger logger = LoggerFactory.getLogger(CacheAfterReturning.class);
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private final static String COLON = ":";
    private final static SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    @AfterReturning(value = "@annotation(com.vanxd.autocache.annotation.Cacheable)", returning = "result")
    public void validtor(JoinPoint joinPoint, Object result) throws Throwable {
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        if (StringUtils.isEmpty(cacheable.table()) && cacheable.tables().length == 0) {
            throw new RuntimeException("注解没有设置表名, 至少设置table或者tables");
        }
        String key = generateKey(joinPoint, method, cacheable);
        logger.debug("auto cache key: {}", key);
        String jsonResult = JSONObject.toJSONString(result);
        saveKey(cacheable, key, jsonResult);
        saveKeyList(cacheable, key, jsonResult);
    }

    /**
     * 把key保存到表keys中
     * @param cacheable
     * @param key
     * @param jsonResult
     */
    private void saveKeyList(Cacheable cacheable, String key, String jsonResult) {
        ZSetOperations<String, String> ops = redisTemplate.opsForZSet();
        Arrays.stream(cacheable.tables())
                .forEach(table -> {
                    String tableKey = table + "~keys";
                    ops.add(tableKey, key, 0);
                });
    }

    /**
     * 保存单独的key
     * @param cacheable
     * @param key
     * @param jsonResult
     */
    private void saveKey(Cacheable cacheable, String key, String jsonResult) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, jsonResult, cacheable.expireSecond(), TimeUnit.SECONDS);
    }

    /**
     * 构造redis key
     * @param joinPoint
     * @param method
     * @param cacheable
     * @return
     */
    private String generateKey(JoinPoint joinPoint, Method method, Cacheable cacheable) {
        String key;
        if (!StringUtils.isEmpty(cacheable.table())) {
            key = cacheable.table();
        } else {
            key = Arrays.stream(cacheable.tables()).collect(Collectors.joining(COLON));
        }
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        JSONArray argArray = JSONArray.parseArray(JSONObject.toJSONString(args));
        if (parameters.length != 0) {
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                String name = parameter.getName();
                Object o = argArray.get(i);
                key += COLON + name + COLON + o.toString();
            }
        }
        return key;
    }
}
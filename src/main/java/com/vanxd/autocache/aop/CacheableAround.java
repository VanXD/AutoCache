package com.vanxd.autocache.aop;

import com.alibaba.fastjson.JSONObject;
import com.vanxd.autocache.annotation.Cacheable;
import com.vanxd.autocache.entity.BaseEntity;
import com.vanxd.autocache.util.KeyGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
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
//        if (cacheable.isCachePut() || StringUtils.isEmpty(cacheResult)) {
            proceedResult = joinPoint.proceed();
            after(key, cacheable, proceedResult, joinPoint.getArgs(), method.getParameters(), method);
            return proceedResult;
//        } else {
//            return JSONObject.parseObject(cacheResult, returnType);
//        }
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

    private void after(String key, Cacheable cacheable, Object result, Object[] args, Parameter[] parameters, Method method) {
        logger.debug("auto cache key: {}", key);
        String dataResult = JSONObject.toJSONString(result);
        if (cacheable.isCachePut()) {
            if (!StringUtils.isEmpty(cacheable.table())) {
                String tableKeyListKey = getTableKeyListKey(cacheable.table());
                String likeKey = KeyGenerator.generateLikeKey(args, method, cacheable);
                refreshCache(tableKeyListKey, likeKey, dataResult, result, cacheable);
            } else {
                Arrays.stream(cacheable.tables())
                      .forEach(talbe -> {
                          String tableKeyListKey = getTableKeyListKey(talbe);
                          String likeKey = KeyGenerator.generateLikeKey(args, method, cacheable);
                          refreshCache(tableKeyListKey, likeKey, dataResult, result, cacheable);
                      });
            }
        } else {
            String unqIdentity;
            if (result instanceof BaseEntity) {
                unqIdentity = ((BaseEntity) result).getId().toString();
            } else {
                unqIdentity = cacheable.key();
            }
            // 保存数据到缓存
            saveKey(cacheable.expireSecond(), key, dataResult);
            // 保存数据key到表key list
            saveKeyList(cacheable, key, unqIdentity);
            // 保存数据key到主键key list
            if (result instanceof BaseEntity) {
                savePrimaryKeyList((BaseEntity) result, key);
            }
        }
    }

    private void savePrimaryKeyList(BaseEntity result, String cacheKey) {
        SetOperations<String, String> ops = redisTemplate.opsForSet();
        String key = "demotest:" + "id:" + result.getId() + "~keys";
        ops.add(key, cacheKey);
        redisTemplate.expire(key, 3600, TimeUnit.SECONDS);
    }

    private void refreshCache(String tableKeyListKey, String likeKey, String dataResultJson, Object dataResult, Cacheable cacheable) {
        // 清除主键缓存
        if (dataResult instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) dataResult;
            String key = "demotest:" + "id:" + baseEntity.getId() + "~keys";
            SetOperations<String, String> setOps = redisTemplate.opsForSet();
            Set<String> members = setOps.members(key);
            if (!CollectionUtils.isEmpty(members)) {
                members.stream().forEach(dataKey -> {
                    saveKey(cacheable.expireSecond(), dataKey, dataResultJson);
                });
            }
        }

        HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
        Cursor<Map.Entry<Object, Object>> scan = ops.scan(tableKeyListKey, ScanOptions.scanOptions().match(likeKey).count(1000).build());
        scan.forEachRemaining(item -> {
            String key = (String) item.getKey();
            // 刷新缓存
            saveKey(cacheable.expireSecond(), key, dataResultJson);
        });

    }

    /**
     * 把key保存到表keys中
     *  @param cacheable
     * @param key
     * @param unqIdentity
     */
    private void saveKeyList(Cacheable cacheable, String key, String unqIdentity) {
        HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
        Arrays.stream(cacheable.tables())
                .forEach(table -> {
                    String tableKey = getTableKeyListKey(table);
                    // 分值是表的数量
                    ops.put(tableKey, key, unqIdentity);
                    redisTemplate.expire(tableKey, 3600, TimeUnit.SECONDS);
                });
    }

    private String getTableKeyListKey(String table) {
        return table + "~keys";
    }

    /**
     * 保存单独的key
     *
     * @param expireSecond
     * @param key
     * @param jsonResult
     */
    private void saveKey(Integer expireSecond, String key, String jsonResult) {
        logger.debug("保存: key: {}, 结果: {}", key, jsonResult);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, jsonResult, expireSecond, TimeUnit.SECONDS);
    }
}
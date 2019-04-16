package com.vanxd.autocache.cache;

import com.alibaba.fastjson.JSONObject;
import com.vanxd.autocache.annotation.Cacheable;
import com.vanxd.autocache.entity.BaseEntity;
import com.vanxd.autocache.util.KeyGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 方法调用后的处理
 */
public class CacheAfterHandle {
    private final static Logger logger = LoggerFactory.getLogger(CacheAfterHandle.class);
    /** 缓存key */
    private String key;
    /** 缓存值 */
    private Cacheable cacheable;
    /** 方法调用结果 */
    private Object result;
    /** AOP参数 */
    private Object[] args;
    /** AOP参数名 */
    private Parameter[] parameters;
    /** AOP方法 */
    private Method method;
    /** AOP连接点 */
    private ProceedingJoinPoint joinPoint;
    /** redis template */
    private RedisTemplate<String, Object> redisTemplate;

    /**
     *  初始化数据
     * @param key               缓存key
     * @param cacheable         注解
     * @param result            目标调用结果
     * @param joinPoint         连接对象
     * @param redisTemplate     redisTemplate
     */
    public CacheAfterHandle(String key, Cacheable cacheable, Object result, ProceedingJoinPoint joinPoint, RedisTemplate redisTemplate) {
        this.key = key;
        this.cacheable = cacheable;
        this.result = result;
        this.joinPoint = joinPoint;
        this.redisTemplate = redisTemplate;
        this.args = joinPoint.getArgs();
        this.method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        this.parameters = this.method.getParameters();
    }

    public void after() {
        logger.debug("auto cache key: {}", key);
        String dataResult = JSONObject.toJSONString(result);
        if (cacheable.isCachePut()) {
            if (!StringUtils.isEmpty(cacheable.table())) {
                String tableKeyListKey = KeyGenerator.getTableKeyListKey(cacheable.table());
                String likeKey = KeyGenerator.generateLikeKey(args, method, cacheable);
                refreshCache(tableKeyListKey, likeKey, dataResult, result, cacheable);
            } else {
                Arrays.stream(cacheable.tables())
                        .forEach(talbe -> {
                            String tableKeyListKey = KeyGenerator.getTableKeyListKey(talbe);
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

    /**
     * 保存单独的key
     *
     * @param expireSecond
     * @param key
     * @param jsonResult
     */
    private void saveKey(Integer expireSecond, String key, String jsonResult) {
        logger.debug("保存: key: {}, 结果: {}", key, jsonResult);
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(key, jsonResult, expireSecond, TimeUnit.SECONDS);
    }

    private void savePrimaryKeyList(BaseEntity result, String cacheKey) {
        SetOperations<String, Object> ops = redisTemplate.opsForSet();
        String key = "demotest:" + "id:" + result.getId() + "~keys";
        ops.add(key, cacheKey);
        redisTemplate.expire(key, 3600, TimeUnit.SECONDS);
    }

    private void refreshCache(String tableKeyListKey, String likeKey, String dataResultJson, Object dataResult, Cacheable cacheable) {
        // 清除主键缓存
        if (dataResult instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) dataResult;
            String key = "demotest:" + "id:" + baseEntity.getId() + "~keys";
            SetOperations<String, Object> setOps = redisTemplate.opsForSet();
            Set<Object> members = setOps.members(key);
            if (!CollectionUtils.isEmpty(members)) {
                members.stream().forEach(dataKey -> {
                    saveKey(cacheable.expireSecond(), dataKey.toString(), dataResultJson);
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
                    String tableKey = KeyGenerator.getTableKeyListKey(table);
                    // 分值是表的数量
                    ops.put(tableKey, key, unqIdentity);
                    redisTemplate.expire(tableKey, 3600, TimeUnit.SECONDS);
                });
    }
}

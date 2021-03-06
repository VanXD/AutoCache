package com.vanxd.autocache.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vanxd.autocache.core.annotation.Cacheable;
import com.vanxd.autocache.core.spel.MethodSpELParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class KeyGenerator {
    private final static Logger logger = LoggerFactory.getLogger(KeyGenerator.class);
    public final static String COLON = ":";
    private final static DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
    /**
     * 构造redis key
     * @param args
     * @param method
     * @param cacheable
     * @return
     */
    public static String generateKey(Object[] args, Method method, Cacheable cacheable) {
        StringBuilder key;
        if (!StringUtils.isEmpty(cacheable.table())) {
            key = new StringBuilder(cacheable.table());
        } else {
            key = new StringBuilder(Arrays.stream(cacheable.tables()).collect(Collectors.joining(COLON)));
        }
        // 无参数缓存
        if (args.length == 0) {
            if (StringUtils.isEmpty(cacheable.key())) {
                throw new RuntimeException("无参数缓存请配置key");
            }
            key.append(COLON).append(cacheable.key());
        } else {
            // 有参数缓存
            String[] parameterNames = nameDiscoverer.getParameterNames(method);
            JSONArray argArray = JSONArray.parseArray(JSONObject.toJSONString(args));
            if (null != parameterNames && parameterNames.length != 0) {
                for (int i = 0; i < parameterNames.length; i++) {
                    String name = parameterNames[i];
                    Object o = argArray.get(i);
                    key.append(COLON).append(name).append(COLON).append(o.toString());
                }
            }
        }
        String result = key.toString();
        logger.debug("生成key: {}", result);
        return result;
    }

    public static String generateLikeKey(Object[] args, Method method, Cacheable cacheable) {
        StringBuilder key = new StringBuilder("*");
        // 无参数缓存
        if (args.length == 0) {
            if (StringUtils.isEmpty(cacheable.key())) {
                throw new RuntimeException("无参数缓存请配置key");
            }
            key.append(COLON).append(cacheable.key());
        } else if (!StringUtils.isEmpty(cacheable.key())){
            MethodSpELParser methodSpELParser = new MethodSpELParser(cacheable.key(), method, args);
            Object value = methodSpELParser.getValue();
            logger.debug("spel value: {}", value);
            return value.toString();
        } else {
            // 有参数缓存
            String[] parameterNames = nameDiscoverer.getParameterNames(method);
            JSONArray argArray = JSONArray.parseArray(JSONObject.toJSONString(args));
            if (null != parameterNames && parameterNames.length != 0) {
                for (int i = 0; i < parameterNames.length; i++) {
                    String name = parameterNames[i];
                    Object o = argArray.get(i);
                    key.append(COLON).append(name).append(COLON).append(o.toString());
                }
            }
        }
        key.append("*");
        String result = key.toString();
        logger.debug("生成like key: {}", result);
        return result;
    }

    public static String getTableKeyListKey(String table) {
        return table + "~keys";
    }
}

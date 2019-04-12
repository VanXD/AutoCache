package com.vanxd.autocache.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vanxd.autocache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class KeyGenerator {
    private final static Logger logger = LoggerFactory.getLogger(KeyGenerator.class);
    private final static String COLON = ":";
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
            Parameter[] parameters = method.getParameters();
            JSONArray argArray = JSONArray.parseArray(JSONObject.toJSONString(args));
            if (parameters.length != 0) {
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    String name = parameter.getName();
                    Object o = argArray.get(i);
                    key.append(COLON).append(name).append(COLON).append(o.toString());
                }
            }
        }
        String result = key.toString();
        logger.debug("auto cache key: {}", result);
        return result;
    }
}

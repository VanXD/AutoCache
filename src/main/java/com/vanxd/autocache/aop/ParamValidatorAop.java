package com.vanxd.autocache.aop;

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
public class ParamValidatorAop {
    Logger logger = LoggerFactory.getLogger(ParamValidatorAop.class);
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private final static String COLON = ":";

    @AfterReturning(value = "execution(* com.vanxd.autocache.dao.*.*(..))", returning = "result")
    public void validtor(JoinPoint joinPoint, Object result) throws Throwable {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        if (null == cacheable) {
            return;
        }
        if (StringUtils.isEmpty(cacheable.table()) && cacheable.tables().length == 0) {
            throw new RuntimeException("注解没有设置表名, 至少设置table或者tables");
        }
        String key;
        if (!StringUtils.isEmpty(cacheable.table())) {
            key = cacheable.table() + COLON;
        } else {
            key = Arrays.stream(cacheable.tables()).collect(Collectors.joining(COLON)) + COLON;
        }
        logger.debug("auto ");
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        String jsonResult = JSONObject.toJSONString(result);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, jsonResult, 1000, TimeUnit.SECONDS);
        System.out.println(key);

//        for (int i = 0; i < args.length;i++) {
//            try {
//                Object target = joinPoint.getTarget();
//                Method validateMethod = target.getClass().getDeclaredMethod(validate.method(), args[i].getClass(), boolean.class);
//                validateMethod.setAccessible(true);
//                validateMethod.invoke(target, args[i], validate.nullAble());
//            } catch (InvocationTargetException e) {
//                Throwable targetException = e.getTargetException();
//                if (targetException instanceof IllegalArgumentException) {
//                    throw targetException;
//                } else {
//                    throw new CustomException("参数校验失败!");
//                }
//            } catch (NoSuchMethodException | IllegalAccessException e) {
//                throw new CustomException("参数校验失败!");
//            }
//        }
    }
}
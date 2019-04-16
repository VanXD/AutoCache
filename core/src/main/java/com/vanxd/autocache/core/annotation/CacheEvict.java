package com.vanxd.autocache.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 记录操作日志的注解
 * 该注解只能用于，后台帐号登录的接口
 * @author wyd on 2017/7/27.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheEvict {
    /** 操作日志 */
    String name();
    /** 操作内容 */
    String content();
    /** APP名称 */
    String appName() default "";
}

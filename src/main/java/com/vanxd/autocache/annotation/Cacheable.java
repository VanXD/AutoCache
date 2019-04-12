package com.vanxd.autocache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 有缓存使用缓存, 无缓存将返回值保存进缓存
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
    /**
     * 关联的表列表
     * @return
     */
    String[] tables() default {};

    /**
     * 关联的表名
     * @return
     */
    String table() default "";

    /**
     * key过期秒数, 默认3600秒(1小时)
     * @return
     */
    int expireSecond() default 3600;
}

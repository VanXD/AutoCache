package com.vanxd.autocache.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 刷新缓存, 从底层获得数据放入缓存
 * 清除缓存需要解决的问题, 缓存是关联表查询的结果, 但是某张表单表更新了数据, 如何去刷新这个缓存
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachePut{
    /**
     * 如果没有参数的话才需要配置这个
     * @return
     */
    String key() default "";
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

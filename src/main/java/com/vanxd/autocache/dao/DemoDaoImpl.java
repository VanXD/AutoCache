package com.vanxd.autocache.dao;

import com.vanxd.autocache.annotation.CachePut;
import com.vanxd.autocache.annotation.Cacheable;
import com.vanxd.autocache.entity.TestDemo;
import org.springframework.stereotype.Component;

@Component
public class DemoDaoImpl implements IDemoDao {


    @Cacheable(tables = {"demo", "demo_aaa"})
    @Override
    public Integer getById(Integer id) {
        return id;
    }

    @Cacheable(tables = {"demo", "demo_aaa"})
    @Override
    public Integer getById(Integer id, String a, TestDemo demo) {
        System.out.println("进入方法了");
        return id;
    }

    @CachePut(table = "demo")
    @Override
    public Integer updateById(Integer id) {
        System.out.println("进入方法了");
        return id + 1;
    }
}

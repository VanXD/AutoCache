package com.vanxd.autocache.dao;

import com.vanxd.autocache.annotation.Cacheable;
import com.vanxd.autocache.entity.TestDemo;
import org.springframework.stereotype.Component;

@Component
public class DemoDaoImpl implements IDemoDao {


    @Cacheable(table = "demo", tables = {"demo", "demo_aaa"})
    @Override
    public Integer getById(Integer id, String a) {
        return id;
    }

    @Cacheable(tables = {"demo", "demo_aaa"})
    @Override
    public Integer getById(Integer id, String a, TestDemo demo) {
        return id;
    }
}

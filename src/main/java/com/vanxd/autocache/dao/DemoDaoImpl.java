package com.vanxd.autocache.dao;

import com.vanxd.autocache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class DemoDaoImpl implements IDemoDao {


    @Cacheable(table = "demo", tables = {"demo", "aaa"}, value = "test")
    @Override
    public Integer getById() {
        return 1;
    }
}

package com.vanxd.autocache.core.dao;

import com.vanxd.autocache.core.entity.TestDemo;

public interface IDemoDao {
    TestDemo getById(Long id);

    TestDemo updateById(TestDemo testDemo);

    TestDemo getByUnion(Long id);

    TestDemo updateByUnion(TestDemo testDemo);
}

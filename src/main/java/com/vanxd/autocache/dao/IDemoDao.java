package com.vanxd.autocache.dao;

import com.vanxd.autocache.entity.TestDemo;

public interface IDemoDao {
    TestDemo getById(Long id);

    TestDemo updateById(TestDemo testDemo);

    TestDemo getByUnion(Long id);

    TestDemo updateByUnion(TestDemo testDemo);
}

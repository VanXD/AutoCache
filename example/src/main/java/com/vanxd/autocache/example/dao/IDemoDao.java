package com.vanxd.autocache.example.dao;

import com.vanxd.autocache.example.entity.TestDemo;

public interface IDemoDao {
    TestDemo getById(Long id);

    TestDemo updateById(TestDemo testDemo);

    TestDemo getByUnion(Long id);

    TestDemo updateByUnion(TestDemo testDemo);
}

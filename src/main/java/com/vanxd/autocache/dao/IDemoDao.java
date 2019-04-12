package com.vanxd.autocache.dao;

import com.vanxd.autocache.entity.TestDemo;

public interface IDemoDao {
    Integer getById(Integer id);

    Integer getById(Integer id, String a, TestDemo demo);

    Integer updateById(Integer id);
}

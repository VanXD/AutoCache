package com.vanxd.autocache.dao;

import com.vanxd.autocache.entity.TestDemo;

public interface IDemoDao {
    Integer getById(Integer id, String a);

    Integer getById(Integer id, String a, TestDemo demo);
}

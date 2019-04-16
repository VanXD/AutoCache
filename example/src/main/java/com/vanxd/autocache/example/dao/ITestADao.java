package com.vanxd.autocache.example.dao;

import com.vanxd.autocache.example.entity.TestA;

public interface ITestADao {
    TestA getById(Long id);

    TestA updateById(TestA testDemo);
}

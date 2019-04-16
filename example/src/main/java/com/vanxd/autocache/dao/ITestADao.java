package com.vanxd.autocache.dao;

import com.vanxd.autocache.entity.TestA;

public interface ITestADao {
    TestA getById(Long id);

    TestA updateById(TestA testDemo);
}

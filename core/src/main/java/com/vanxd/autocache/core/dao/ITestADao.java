package com.vanxd.autocache.core.dao;

import com.vanxd.autocache.core.entity.TestA;

public interface ITestADao {
    TestA getById(Long id);

    TestA updateById(TestA testDemo);
}

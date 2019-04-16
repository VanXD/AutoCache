package com.vanxd.autocache.example.dao;

import com.vanxd.autocache.core.annotation.Cacheable;
import com.vanxd.autocache.example.entity.TestA;

public class TestDaoImpl implements ITestADao {
    @Cacheable(table = "testa")
    @Override
    public TestA getById(Long id) {
        TestA testA = new TestA();
        testA.setId(id);
        testA.setTestaName("1");
        testA.setTestaInteger(1);
        testA.setTestaBoolean(true);
        return testA;
    }

    @Cacheable(table = "testa", isCachePut = true)
    @Override
    public TestA updateById(TestA testA) {
        return testA;
    }
}

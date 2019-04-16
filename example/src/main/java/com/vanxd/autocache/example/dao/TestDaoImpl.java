package com.vanxd.autocache.example.dao;

import com.vanxd.autocache.core.annotation.Cacheable;
import com.vanxd.autocache.example.entity.TestA;
import org.springframework.stereotype.Component;

@Component
public class TestDaoImpl implements ITestADao {
    @Cacheable(table = "demo_2")
    @Override
    public TestA getById(Long id) {
        TestA testA = new TestA();
        testA.setId(id);
        testA.setTestaName("1");
        testA.setTestaInteger(1);
        testA.setTestaBoolean(true);
        return testA;
    }

    @Cacheable(table = "demo_2", key = "'id:' + #testA.id", isCachePut = true)
    @Override
    public TestA updateById(TestA testA) {
        return testA;
    }
}

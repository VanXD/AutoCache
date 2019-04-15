package com.vanxd.autocache.dao;

import com.vanxd.autocache.annotation.Cacheable;
import com.vanxd.autocache.entity.TestDemo;
import org.springframework.stereotype.Component;

@Component
public class DemoDaoImpl implements IDemoDao {


    @Cacheable(table = "demo_1")
    @Override
    public TestDemo getById(Long id) {
        TestDemo testDemo = new TestDemo();
        testDemo.setId(id);
        return testDemo;
    }

    @Cacheable(table = "demo_1", isCachePut = true)
    @Override
    public TestDemo updateById(TestDemo testDemo) {
        return testDemo;
    }

    @Cacheable(tables = {"demo_1", "demo_2"})
    @Override
    public TestDemo getByUnion(Long id) {
        TestDemo testDemo = new TestDemo();
        testDemo.setId(id);
        testDemo.setA("UNION");
        testDemo.setB(1);
        return testDemo;
    }

    @Cacheable(tables = {"demo_1", "demo_2"}, isCachePut = true)
    @Override
    public TestDemo updateByUnion(TestDemo testDemo) {
        return testDemo;
    }
}

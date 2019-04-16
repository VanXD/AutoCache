package com.vanxd.autocache.example.dao;

import com.vanxd.autocache.core.annotation.Cacheable;
import com.vanxd.autocache.example.entity.TestA;
import com.vanxd.autocache.example.entity.TestDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DemoDaoImpl implements IDemoDao {
    @Autowired
    private ITestADao testADao;

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
        TestA byId = testADao.getById(1111L);
        TestDemo testDemo = new TestDemo();
        testDemo.setId(id);
        testDemo.setA("UNION");
        testDemo.setB(1);
        testDemo.setTestaName(byId.getTestaName());
        return testDemo;
    }

    @Cacheable(tables = {"demo_1", "demo_2"}, isCachePut = true)
    @Override
    public TestDemo updateByUnion(TestDemo testDemo) {
        return testDemo;
    }
}

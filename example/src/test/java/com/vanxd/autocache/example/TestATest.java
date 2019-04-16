package com.vanxd.autocache.example;

import com.vanxd.autocache.example.dao.ITestADao;
import com.vanxd.autocache.example.entity.TestA;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 测试
 *
 */
public class TestATest extends ExampleApplicationTests {
    @Autowired
    private ITestADao testADao;

    @Test
    public void test() {
        testADao.getById(1111L);
    }

    @Test
    public void test2() {
        TestA testDemo = new TestA();
        testDemo.setId(1111L);
        testDemo.setTestaBoolean(false);
        testDemo.setTestaInteger(-1);
        testDemo.setTestaName("test update");
        testADao.updateById(testDemo);
    }
}

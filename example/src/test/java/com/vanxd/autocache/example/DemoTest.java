package com.vanxd.autocache.example;

import com.vanxd.autocache.example.dao.IDemoDao;
import com.vanxd.autocache.example.entity.TestDemo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 测试
 *
 */
public class DemoTest extends ExampleApplicationTests {
    @Autowired
    private IDemoDao demoDao;

    @Test
    public void test() {
        System.out.println(demoDao.getById(12L));
    }

    @Test
    public void test2() {
        TestDemo testDemo = new TestDemo();
        testDemo.setId(12L);
        testDemo.setA("AA");
        System.out.println(demoDao.updateById(testDemo));
    }

    @Test
    public void test3() {
        TestDemo byUnion = demoDao.getByUnion(999L);
    }

    @Test
    public void test4() {
        TestDemo testDemo = new TestDemo();
        testDemo.setId(999L);
        testDemo.setA("UNION UPDATE");
        testDemo.setB(2);
        testDemo.setC(true);
        demoDao.updateByUnion(testDemo);
    }

}

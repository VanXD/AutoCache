package com.vanxd.autocache;

import com.vanxd.autocache.dao.IDemoDao;
import com.vanxd.autocache.entity.TestDemo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 测试
 *
 */
public class SimpleTest extends AutoCacheApplicationTests {
    @Autowired
    private IDemoDao demoDao;

    @Test
    public void test() {
        System.out.println(demoDao.getById(12));
    }

    @Test
    public void test2() {
        TestDemo testDemo = new TestDemo();
        testDemo.setA("1");
        testDemo.setB(2);
//        testDemo.setC(true);
        System.out.println(demoDao.getById(12, "1", testDemo));
    }

    @Test
    public void test3() {
        System.out.println(demoDao.updateById(12));
    }

}

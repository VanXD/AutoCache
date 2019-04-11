package com.vanxd.autocache;

import com.vanxd.autocache.dao.IDemoDao;
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
        System.out.println(demoDao.getById());
    }

}

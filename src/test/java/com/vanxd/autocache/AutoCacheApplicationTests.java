package com.vanxd.autocache;

import com.vanxd.autocache.start.AutoCacheApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AutoCacheApplication.class)
//@Transactional
//@Rollback
//@ActiveProfilesofiles(profiles = "test") // 在这里切换测试时的配置文件
public class AutoCacheApplicationTests {

	@Test
	public void contextLoads() {
	}

}

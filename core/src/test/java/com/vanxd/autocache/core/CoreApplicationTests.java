package com.vanxd.autocache.core;

import com.vanxd.autocache.core.start.CoreApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreApplication.class)
//@Transactional
//@Rollback
//@ActiveProfilesofiles(profiles = "test") // 在这里切换测试时的配置文件
public class CoreApplicationTests {

	@Test
	public void contextLoads() {
	}

}

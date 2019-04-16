package com.vanxd.autocache.example;

import com.vanxd.autocache.example.start.ExampleApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class)
//@Transactional
//@Rollback
//@ActiveProfilesofiles(profiles = "test") // 在这里切换测试时的配置文件
public class ExampleApplicationTests {
}

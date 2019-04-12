package com.vanxd.autocache.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
@ComponentScan("com.vanxd.autocache")
public class AutoCacheApplication {
	private static final ReentrantLock LOCK = new ReentrantLock();
	private static final Condition STOP = LOCK.newCondition();

	public static void main(String[] args) {
		SpringApplication.run(AutoCacheApplication.class, args);
	}

}

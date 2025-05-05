package com.ninjaone.dundie_awards;

import com.ninjaone.dundie_awards.infrastructure.cache.AwardsRedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DundieAwardsApplication implements ApplicationRunner {

	@Autowired
	private AwardsRedisCache awardsRedisCache;

	public static void main(String[] args) {
		SpringApplication.run(DundieAwardsApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		awardsRedisCache.resetCounter();
	}

}

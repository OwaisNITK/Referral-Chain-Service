package com.onmobile.vol.referralchain.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAutoConfiguration
@EnableScheduling
@EntityScan(basePackages ="com.onmobile.vol.referralchain.app.dataaccess.domain")
@EnableJpaRepositories(basePackages = "com.onmobile.vol.referralchain.app.dataaccess.repository")
@SpringBootApplication
public class ReferralChainApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ReferralChainApplication.class, args);
	}
}
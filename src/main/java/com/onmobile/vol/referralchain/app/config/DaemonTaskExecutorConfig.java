package com.onmobile.vol.referralchain.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class DaemonTaskExecutorConfig{

	@Bean
	@ConfigurationProperties(prefix = "referralchain.sms-notification-daemon.task-executor")
	public ThreadPoolTaskExecutor smsNotificationDaemonTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setRejectedExecutionHandler(callerRunsPolicyWaitForSpace());
		return taskExecutor;
	}

	public CallerRunsPolicyWaitForSpace callerRunsPolicyWaitForSpace() {
		return new CallerRunsPolicyWaitForSpace();
	}
}
package com.onmobile.vol.referralchain.app.daemon;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
	
	@Value("${referralchain.scheduler.poolSize:2}")
	private int poolSize;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		
         ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
         scheduler.setPoolSize(poolSize);
         scheduler.setThreadNamePrefix("referralchain-scheduled-task-pool-");
         scheduler.initialize();

         taskRegistrar.setTaskScheduler(scheduler);
	}
}
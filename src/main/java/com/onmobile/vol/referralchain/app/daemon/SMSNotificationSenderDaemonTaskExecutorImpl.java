package com.onmobile.vol.referralchain.app.daemon;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration("smsNotificationSenderDaemonTaskExecutor")
public class SMSNotificationSenderDaemonTaskExecutorImpl implements TaskExecutor {
	 
	@Resource(name = "smsNotificationDaemonTaskExecutor")
	private ThreadPoolTaskExecutor smsNotificationDaemonTaskExecutor;

	    @Override
	    public void execute(Runnable task) {
	    	smsNotificationDaemonTaskExecutor.execute(task);

	    }

	    @Override
	    public <T> Future<T> submit(Callable<T> task) {
	        return smsNotificationDaemonTaskExecutor.submit(task);
	    }

	    @Override
	    public ThreadPoolExecutor getThreadPoolExecutor() {
	        return smsNotificationDaemonTaskExecutor.getThreadPoolExecutor();
	    }

}
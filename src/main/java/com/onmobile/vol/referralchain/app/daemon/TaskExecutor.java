package com.onmobile.vol.referralchain.app.daemon;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public interface TaskExecutor {
	
	  void execute(Runnable task);
	  
	  <T> Future<T> submit(Callable<T> task);
	  
	  ThreadPoolExecutor getThreadPoolExecutor();
}
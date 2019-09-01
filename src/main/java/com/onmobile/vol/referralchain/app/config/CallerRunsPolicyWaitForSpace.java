package com.onmobile.vol.referralchain.app.config;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class CallerRunsPolicyWaitForSpace implements RejectedExecutionHandler {

	public CallerRunsPolicyWaitForSpace() {
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor tpe) {
		boolean interrupted = false;
		try {
			while (true) {
				try {
					tpe.getQueue().put(r);
					return;
				} catch (InterruptedException ex) {
					interrupted = true;
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
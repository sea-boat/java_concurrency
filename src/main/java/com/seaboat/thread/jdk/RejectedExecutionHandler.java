package com.seaboat.thread.jdk;

public interface RejectedExecutionHandler {

	void rejectedExecution(Runnable r, ThreadPoolExecutor executor);
}

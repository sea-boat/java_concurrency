package com.seaboat.thread.jdk;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public interface ExecutorService extends Executor {

	void shutdown();

	boolean isShutdown();

	boolean isTerminated();

	void awaitTermination() throws InterruptedException;

	<T> Future<T> submit(Callable<T> task);

}

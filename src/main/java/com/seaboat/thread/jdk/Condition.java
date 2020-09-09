package com.seaboat.thread.jdk;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface Condition {

	void await() throws InterruptedException;

	void awaitUninterruptibly();

	long awaitNanos(long nanosTimeout) throws InterruptedException;

	boolean await(long time, TimeUnit unit) throws InterruptedException;

	boolean awaitUntil(Date deadline) throws InterruptedException;

	void signal();

	void signalAll();
}

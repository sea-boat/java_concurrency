package com.seaboat.thread.jdk;

import java.util.concurrent.TimeUnit;

public interface BlockingQueue<E> {
	void put(E e) throws InterruptedException;
	E take() throws InterruptedException;
	boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException;
	E poll(long timeout, TimeUnit unit) throws InterruptedException;
	int size();
}

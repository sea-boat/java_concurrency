package com.seaboat.thread.jdk;

import java.util.concurrent.TimeUnit;

public interface BlockingDeque<E> {

	void put(E e) throws InterruptedException;

	void putFirst(E e) throws InterruptedException;

	void putLast(E e) throws InterruptedException;

	E take() throws InterruptedException;

	E takeFirst() throws InterruptedException;

	E takeLast() throws InterruptedException;

	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException;

	boolean offerFirst(E e, long timeout, TimeUnit unit) throws InterruptedException;

	boolean offerLast(E e, long timeout, TimeUnit unit) throws InterruptedException;

	public E poll(long timeout, TimeUnit unit) throws InterruptedException;

	E pollFirst(long timeout, TimeUnit unit) throws InterruptedException;

	E pollLast(long timeout, TimeUnit unit) throws InterruptedException;

}

package com.seaboat.thread.jdk;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrayBlockingQueue<E> implements BlockingQueue<E> {

	int count;
	int takeIndex;
	int putIndex;
	final Object[] items;
	final ReentrantLock lock;
	private final Condition notEmpty;
	private final Condition notFull;

	public ArrayBlockingQueue(int capacity) {
		this(capacity, false);
	}

	public ArrayBlockingQueue(int capacity, boolean fair) {
		if (capacity <= 0)
			throw new IllegalArgumentException();
		this.items = new Object[capacity];
		lock = new ReentrantLock(fair);
		notEmpty = lock.newCondition();
		notFull = lock.newCondition();
	}

	private void enqueue(E e) {
		final Object[] items = this.items;
		items[putIndex] = e;
		if (++putIndex == items.length)
			putIndex = 0;
		count++;
		notEmpty.signal();
	}

	private E dequeue() {
		final Object[] items = this.items;
		E e = (E) items[takeIndex];
		items[takeIndex] = null;
		if (++takeIndex == items.length)
			takeIndex = 0;
		count--;
		notFull.signal();
		return e;
	}

	public int size() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return count;
		} finally {
			lock.unlock();
		}
	}

	public void put(E e) throws InterruptedException {
		Objects.requireNonNull(e);
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			while (count == items.length)
				notFull.await();
			enqueue(e);
		} finally {
			lock.unlock();
		}
	}

	public E take() throws InterruptedException {
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			while (count == 0)
				notEmpty.await();
			return dequeue();
		} finally {
			lock.unlock();
		}
	}

	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		Objects.requireNonNull(e);
		long nanos = unit.toNanos(timeout);
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			while (count == items.length) {
				if (nanos <= 0L)
					return false;
				nanos = notFull.awaitNanos(nanos);
			}
			enqueue(e);
			return true;
		} finally {
			lock.unlock();
		}
	}

	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		long nanos = unit.toNanos(timeout);
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			while (count == 0) {
				if (nanos <= 0L)
					return null;
				nanos = notEmpty.awaitNanos(nanos);
			}
			return dequeue();
		} finally {
			lock.unlock();
		}
	}

}

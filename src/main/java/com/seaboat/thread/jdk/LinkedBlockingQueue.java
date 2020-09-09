package com.seaboat.thread.jdk;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedBlockingQueue<E> implements BlockingQueue<E> {

	static class Node<E> {
		E item;
		Node<E> next;

		Node(E x) {
			item = x;
		}
	}

	private final int capacity;
	transient Node<E> head;
	private transient Node<E> last;
	private final AtomicInteger count = new AtomicInteger();
	private final ReentrantLock takeLock = new ReentrantLock();
	private final Condition notEmpty = takeLock.newCondition();
	private final ReentrantLock putLock = new ReentrantLock();
	private final Condition notFull = putLock.newCondition();

	public LinkedBlockingQueue() {
		this(Integer.MAX_VALUE);
	}

	public LinkedBlockingQueue(int capacity) {
		if (capacity <= 0)
			throw new IllegalArgumentException();
		this.capacity = capacity;
		last = head = new Node<E>(null);
	}

	private void enqueue(Node<E> node) {
		last = last.next = node;
	}

	private E dequeue() {
		Node<E> h = head;
		Node<E> first = h.next;
		h.next = h;
		head = first;
		E x = first.item;
		first.item = null;
		return x;
	}

	public int size() {
		return count.get();
	}

	public void put(E e) throws InterruptedException {
		if (e == null)
			throw new NullPointerException();
		final int c;
		final Node<E> node = new Node<E>(e);
		final ReentrantLock putLock = this.putLock;
		final AtomicInteger count = this.count;
		putLock.lockInterruptibly();
		try {
			while (count.get() == capacity) {
				notFull.await();
			}
			enqueue(node);
			c = count.getAndIncrement();
			if (c + 1 < capacity)
				notFull.signal();
		} finally {
			putLock.unlock();
		}
		if (c == 0) {
			final ReentrantLock takeLock = this.takeLock;
			takeLock.lock();
			try {
				notEmpty.signal();
			} finally {
				takeLock.unlock();
			}
		}
	}

	public E take() throws InterruptedException {
		final E x;
		final int c;
		final AtomicInteger count = this.count;
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lockInterruptibly();
		try {
			while (count.get() == 0) {
				notEmpty.await();
			}
			x = dequeue();
			c = count.getAndDecrement();
			if (c > 1)
				notEmpty.signal();
		} finally {
			takeLock.unlock();
		}
		if (c == capacity) {
			final ReentrantLock putLock = this.putLock;
			putLock.lock();
			try {
				notFull.signal();
			} finally {
				putLock.unlock();
			}
		}
		return x;
	}

	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		if (e == null)
			throw new NullPointerException();
		long nanos = unit.toNanos(timeout);
		final int c;
		final ReentrantLock putLock = this.putLock;
		final AtomicInteger count = this.count;
		putLock.lockInterruptibly();
		try {
			while (count.get() == capacity) {
				if (nanos <= 0L)
					return false;
				nanos = notFull.awaitNanos(nanos);
			}
			enqueue(new Node<E>(e));
			c = count.getAndIncrement();
			if (c + 1 < capacity)
				notFull.signal();
		} finally {
			putLock.unlock();
		}
		if (c == 0) {
			final ReentrantLock takeLock = this.takeLock;
			takeLock.lock();
			try {
				notEmpty.signal();
			} finally {
				takeLock.unlock();
			}
		}
		return true;
	}

	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		final E x;
		final int c;
		long nanos = unit.toNanos(timeout);
		final AtomicInteger count = this.count;
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lockInterruptibly();
		try {
			while (count.get() == 0) {
				if (nanos <= 0L)
					return null;
				nanos = notEmpty.awaitNanos(nanos);
			}
			x = dequeue();
			c = count.getAndDecrement();
			if (c > 1)
				notEmpty.signal();
		} finally {
			takeLock.unlock();
		}
		if (c == capacity) {
			final ReentrantLock putLock = this.putLock;
			putLock.lock();
			try {
				notFull.signal();
			} finally {
				putLock.unlock();
			}
		}
		return x;
	}

}

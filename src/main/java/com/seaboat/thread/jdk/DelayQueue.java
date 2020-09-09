package com.seaboat.thread.jdk;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DelayQueue<E extends Delayed> implements BlockingQueue<E> {
	private Thread leader;
	private final transient ReentrantLock lock = new ReentrantLock();
	private final Condition available = lock.newCondition();
	private final PriorityQueue<E> q = new PriorityQueue<E>();

	public DelayQueue() {
	}

	public void put(E e) {
		offer(e);
	}

	public boolean offer(E e) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			q.offer(e);
			if (q.peek() == e) {
				leader = null;
				available.signal();
			}
			return true;
		} finally {
			lock.unlock();
		}
	}

	public boolean offer(E e, long timeout, TimeUnit unit) {
		return offer(e);
	}

	public E take() throws InterruptedException {
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			for (;;) {
				E first = q.peek();
				if (first == null)
					available.await();
				else {
					long delay = first.getDelay(NANOSECONDS);
					if (delay <= 0L)
						return q.poll();
					first = null;
					if (leader != null)
						available.await();
					else {
						Thread thisThread = Thread.currentThread();
						leader = thisThread;
						try {
							available.awaitNanos(delay);
						} finally {
							if (leader == thisThread)
								leader = null;
						}
					}
				}
			}
		} finally {
			if (leader == null && q.peek() != null)
				available.signal();
			lock.unlock();
		}
	}

	public E poll() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			E first = q.peek();
			return (first == null || first.getDelay(NANOSECONDS) > 0) ? null : q.poll();
		} finally {
			lock.unlock();
		}
	}

	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		long nanos = unit.toNanos(timeout);
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			for (;;) {
				E first = q.peek();
				if (first == null) {
					if (nanos <= 0L)
						return null;
					else
						nanos = available.awaitNanos(nanos);
				} else {
					long delay = first.getDelay(NANOSECONDS);
					if (delay <= 0L)
						return q.poll();
					if (nanos <= 0L)
						return null;
					first = null;
					if (nanos < delay || leader != null)
						nanos = available.awaitNanos(nanos);
					else {
						Thread thisThread = Thread.currentThread();
						leader = thisThread;
						try {
							long timeLeft = available.awaitNanos(delay);
							nanos -= delay - timeLeft;
						} finally {
							if (leader == thisThread)
								leader = null;
						}
					}
				}
			}
		} finally {
			if (leader == null && q.peek() != null)
				available.signal();
			lock.unlock();
		}
	}

	public int size() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return q.size();
		} finally {
			lock.unlock();
		}
	}

}

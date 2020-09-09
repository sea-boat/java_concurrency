package com.seaboat.thread.jdk;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedBlockingDeque<E> implements BlockingDeque<E> {
	static final class Node<E> {
		E item;
		Node<E> prev;
		Node<E> next;

		Node(E x) {
			item = x;
		}
	}

	transient Node<E> first;
	transient Node<E> last;
	private transient int count;
	private final int capacity;
	final ReentrantLock lock = new ReentrantLock();
	private final Condition notEmpty = lock.newCondition();
	private final Condition notFull = lock.newCondition();

	public LinkedBlockingDeque() {
		this(Integer.MAX_VALUE);
	}

	public LinkedBlockingDeque(int capacity) {
		if (capacity <= 0)
			throw new IllegalArgumentException();
		this.capacity = capacity;
	}

	private boolean linkFirst(Node<E> node) {
		if (count >= capacity)
			return false;
		Node<E> f = first;
		node.next = f;
		first = node;
		if (last == null)
			last = node;
		else
			f.prev = node;
		++count;
		notEmpty.signal();
		return true;
	}

	private boolean linkLast(Node<E> node) {
		if (count >= capacity)
			return false;
		Node<E> l = last;
		node.prev = l;
		last = node;
		if (first == null)
			first = node;
		else
			l.next = node;
		++count;
		notEmpty.signal();
		return true;
	}

	private E unlinkFirst() {
		Node<E> f = first;
		if (f == null)
			return null;
		Node<E> n = f.next;
		E item = f.item;
		f.item = null;
		f.next = f;
		first = n;
		if (n == null)
			last = null;
		else
			n.prev = null;
		--count;
		notFull.signal();
		return item;
	}

	private E unlinkLast() {
		Node<E> l = last;
		if (l == null)
			return null;
		Node<E> p = l.prev;
		E item = l.item;
		l.item = null;
		l.prev = l;
		last = p;
		if (p == null)
			first = null;
		else
			p.next = null;
		--count;
		notFull.signal();
		return item;
	}

	public void put(E e) throws InterruptedException {
		putLast(e);
	}

	public void putFirst(E e) throws InterruptedException {
		if (e == null)
			throw new NullPointerException();
		Node<E> node = new Node<E>(e);
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			while (!linkFirst(node))
				notFull.await();
		} finally {
			lock.unlock();
		}
	}

	public void putLast(E e) throws InterruptedException {
		if (e == null)
			throw new NullPointerException();
		Node<E> node = new Node<E>(e);
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			while (!linkLast(node))
				notFull.await();
		} finally {
			lock.unlock();
		}
	}

	public E take() throws InterruptedException {
		return takeFirst();
	}

	public E takeFirst() throws InterruptedException {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			E x;
			while ((x = unlinkFirst()) == null)
				notEmpty.await();
			return x;
		} finally {
			lock.unlock();
		}
	}

	public E takeLast() throws InterruptedException {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			E x;
			while ((x = unlinkLast()) == null)
				notEmpty.await();
			return x;
		} finally {
			lock.unlock();
		}
	}

	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		return offerLast(e, timeout, unit);
	}

	public boolean offerFirst(E e, long timeout, TimeUnit unit) throws InterruptedException {
		if (e == null)
			throw new NullPointerException();
		Node<E> node = new Node<E>(e);
		long nanos = unit.toNanos(timeout);
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			while (!linkFirst(node)) {
				if (nanos <= 0L)
					return false;
				nanos = notFull.awaitNanos(nanos);
			}
			return true;
		} finally {
			lock.unlock();
		}
	}

	public boolean offerLast(E e, long timeout, TimeUnit unit) throws InterruptedException {
		if (e == null)
			throw new NullPointerException();
		Node<E> node = new Node<E>(e);
		long nanos = unit.toNanos(timeout);
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			while (!linkLast(node)) {
				if (nanos <= 0L)
					return false;
				nanos = notFull.awaitNanos(nanos);
			}
			return true;
		} finally {
			lock.unlock();
		}
	}

	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		return pollFirst(timeout, unit);
	}

	public E pollFirst(long timeout, TimeUnit unit) throws InterruptedException {
		long nanos = unit.toNanos(timeout);
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			E x;
			while ((x = unlinkFirst()) == null) {
				if (nanos <= 0L)
					return null;
				nanos = notEmpty.awaitNanos(nanos);
			}
			return x;
		} finally {
			lock.unlock();
		}
	}

	public E pollLast(long timeout, TimeUnit unit) throws InterruptedException {
		long nanos = unit.toNanos(timeout);
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			E x;
			while ((x = unlinkLast()) == null) {
				if (nanos <= 0L)
					return null;
				nanos = notEmpty.awaitNanos(nanos);
			}
			return x;
		} finally {
			lock.unlock();
		}
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

}

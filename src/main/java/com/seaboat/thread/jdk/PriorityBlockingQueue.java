package com.seaboat.thread.jdk;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PriorityBlockingQueue<E> implements BlockingQueue<E> {

	private static final int DEFAULT_INITIAL_CAPACITY = 11;
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
	private transient Object[] queue;
	private transient int size;
	private transient Comparator<? super E> comparator;
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition notEmpty = lock.newCondition();
	private transient volatile int allocationSpinLock;
	private static final VarHandle ALLOCATIONSPINLOCK;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			ALLOCATIONSPINLOCK = l.findVarHandle(PriorityBlockingQueue.class, "allocationSpinLock",
					int.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public PriorityBlockingQueue() {
		this(DEFAULT_INITIAL_CAPACITY, null);
	}

	public PriorityBlockingQueue(int initialCapacity) {
		this(initialCapacity, null);
	}

	public PriorityBlockingQueue(int initialCapacity, Comparator<? super E> comparator) {
		if (initialCapacity < 1)
			throw new IllegalArgumentException();
		this.comparator = comparator;
		this.queue = new Object[Math.max(1, initialCapacity)];
	}

	private static <T> void siftUpComparable(int k, T x, Object[] es) {
		Comparable<? super T> key = (Comparable<? super T>) x;
		while (k > 0) {
			int parent = (k - 1) >>> 1;
			Object e = es[parent];
			if (key.compareTo((T) e) >= 0)
				break;
			es[k] = e;
			k = parent;
		}
		es[k] = key;
	}

	private static <T> void siftDownComparable(int k, T x, Object[] es, int n) {
		Comparable<? super T> key = (Comparable<? super T>) x;
		int half = n >>> 1;
		while (k < half) {
			int child = (k << 1) + 1;
			Object c = es[child];
			int right = child + 1;
			if (right < n && ((Comparable<? super T>) c).compareTo((T) es[right]) > 0)
				c = es[child = right];
			if (key.compareTo((T) c) <= 0)
				break;
			es[k] = c;
			k = child;
		}
		es[k] = key;
	}

	private static <T> void siftUpUsingComparator(int k, T x, Object[] es,
			Comparator<? super T> cmp) {
		while (k > 0) {
			int parent = (k - 1) >>> 1;
			Object e = es[parent];
			if (cmp.compare(x, (T) e) >= 0)
				break;
			es[k] = e;
			k = parent;
		}
		es[k] = x;
	}

	private static <T> void siftDownUsingComparator(int k, T x, Object[] es, int n,
			Comparator<? super T> cmp) {
		int half = n >>> 1;
		while (k < half) {
			int child = (k << 1) + 1;
			Object c = es[child];
			int right = child + 1;
			if (right < n && cmp.compare((T) c, (T) es[right]) > 0)
				c = es[child = right];
			if (cmp.compare(x, (T) c) <= 0)
				break;
			es[k] = c;
			k = child;
		}
		es[k] = x;
	}

	public void put(E e) {
		offer(e);
	}

	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		return offer(e);
	}

	public boolean offer(E e) {
		if (e == null)
			throw new NullPointerException();
		final ReentrantLock lock = this.lock;
		lock.lock();
		int n, cap;
		Object[] es;
		while ((n = size) >= (cap = (es = queue).length))
			tryGrow(es, cap);
		try {
			final Comparator<? super E> cmp;
			if ((cmp = comparator) == null)
				siftUpComparable(n, e, es);
			else
				siftUpUsingComparator(n, e, es, cmp);
			size = n + 1;
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
		return true;
	}

	private void tryGrow(Object[] array, int oldCap) {
		lock.unlock();
		Object[] newArray = null;
		if (allocationSpinLock == 0 && ALLOCATIONSPINLOCK.compareAndSet(this, 0, 1)) {
			try {
				int newCap = oldCap + ((oldCap < 64) ? (oldCap + 2) : (oldCap >> 1));
				if (newCap - MAX_ARRAY_SIZE > 0) {
					int minCap = oldCap + 1;
					if (minCap < 0 || minCap > MAX_ARRAY_SIZE)
						throw new OutOfMemoryError();
					newCap = MAX_ARRAY_SIZE;
				}
				if (newCap > oldCap && queue == array)
					newArray = new Object[newCap];
			} finally {
				allocationSpinLock = 0;
			}
		}
		if (newArray == null)
			Thread.yield();
		lock.lock();
		if (newArray != null && queue == array) {
			queue = newArray;
			System.arraycopy(array, 0, newArray, 0, oldCap);
		}
	}

	public E take() throws InterruptedException {
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		E result;
		try {
			while ((result = dequeue()) == null)
				notEmpty.await();
		} finally {
			lock.unlock();
		}
		return result;
	}

	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		long nanos = unit.toNanos(timeout);
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		E result;
		try {
			while ((result = dequeue()) == null && nanos > 0)
				nanos = notEmpty.awaitNanos(nanos);
		} finally {
			lock.unlock();
		}
		return result;
	}

	private E dequeue() {
		final Object[] es;
		final E result;
		if ((result = (E) ((es = queue)[0])) != null) {
			final int n;
			final E x = (E) es[(n = --size)];
			es[n] = null;
			if (n > 0) {
				final Comparator<? super E> cmp;
				if ((cmp = comparator) == null)
					siftDownComparable(0, x, es, n);
				else
					siftDownUsingComparator(0, x, es, n, cmp);
			}
		}
		return result;
	}

	public int size() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return size;
		} finally {
			lock.unlock();
		}
	}

}

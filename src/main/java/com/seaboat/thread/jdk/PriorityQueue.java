package com.seaboat.thread.jdk;

import java.util.Arrays;
import java.util.Comparator;

public class PriorityQueue<E> {
	int size;
	transient Object[] queue;
	private static final int DEFAULT_INITIAL_CAPACITY = 11;
	private final Comparator<? super E> comparator;
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	public PriorityQueue() {
		this(DEFAULT_INITIAL_CAPACITY, null);
	}

	public PriorityQueue(int initialCapacity) {
		this(initialCapacity, null);
	}

	public PriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
		if (initialCapacity < 1)
			throw new IllegalArgumentException();
		this.queue = new Object[initialCapacity];
		this.comparator = comparator;
	}

	public boolean offer(E e) {
		if (e == null)
			throw new NullPointerException();
		int i = size;
		if (i >= queue.length)
			grow(i + 1);
		if (comparator != null)
			siftUpUsingComparator(i, e, queue, comparator);
		else
			siftUpComparable(i, e, queue);
		size = i + 1;
		return true;
	}

	private void grow(int minCapacity) {
		int oldCapacity = queue.length;
		int newCapacity = oldCapacity
				+ ((oldCapacity < 64) ? (oldCapacity + 2) : (oldCapacity >> 1));
		if (minCapacity < 0)
			throw new OutOfMemoryError();
		newCapacity = (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
		queue = Arrays.copyOf(queue, newCapacity);
	}

	public E poll() {
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

	public E peek() {
		return (E) queue[0];
	}

	public int size() {
		return size;
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

}

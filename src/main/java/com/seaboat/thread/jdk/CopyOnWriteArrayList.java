package com.seaboat.thread.jdk;

import java.util.Arrays;

public class CopyOnWriteArrayList<E> {

	final transient Object lock = new Object();
	private transient volatile Object[] array;

	public CopyOnWriteArrayList() {
		array = new Object[0];
	}

	public CopyOnWriteArrayList(E[] toCopyIn) {
		array = Arrays.copyOf(toCopyIn, toCopyIn.length, Object[].class);
	}

	public boolean add(E e) {
		synchronized (lock) {
			Object[] es = array;
			int len = es.length;
			es = Arrays.copyOf(es, len + 1);
			es[len] = e;
			array = es;
			return true;
		}
	}

	public void add(int index, E element) {
		synchronized (lock) {
			Object[] es = array;
			int len = es.length;
			if (index > len || index < 0)
				throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + len);
			Object[] newElements;
			int numMoved = len - index;
			if (numMoved == 0)
				newElements = Arrays.copyOf(es, len + 1);
			else {
				newElements = new Object[len + 1];
				System.arraycopy(es, 0, newElements, 0, index);
				System.arraycopy(es, index, newElements, index + 1, numMoved);
			}
			newElements[index] = element;
			array = newElements;
		}
	}

	public E remove(int index) {
		synchronized (lock) {
			Object[] es = array;
			int len = es.length;
			E oldValue = (E) array[index];
			int numMoved = len - index - 1;
			Object[] newElements;
			if (numMoved == 0)
				newElements = Arrays.copyOf(es, len - 1);
			else {
				newElements = new Object[len - 1];
				System.arraycopy(es, 0, newElements, 0, index);
				System.arraycopy(es, index + 1, newElements, index, numMoved);
			}
			array = newElements;
			return oldValue;
		}
	}

	public E set(int index, E element) {
		synchronized (lock) {
			Object[] es = array;
			E oldValue = (E) array[index];
			if (oldValue != element) {
				es = es.clone();
				es[index] = element;
				array = es;
			}
			return oldValue;
		}
	}

	public E get(int index) {
		return (E) array[index];
	}

	public int size() {
		return array.length;
	}

}
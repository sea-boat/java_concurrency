package com.seaboat.thread.jdk;

import java.util.Hashtable;
import java.util.Map;

public class ThreadLocal<T> {
	static Map<Thread, Object> map = new Hashtable<Thread, Object>();

	public void set(T obj) {
		map.put(Thread.currentThread(), obj);
	}

	public T get() {
		return (T) map.get(Thread.currentThread());
	}

	public void remove() {
		map.remove(Thread.currentThread());
	}
}
package com.seaboat.thread;

import com.seaboat.thread.jdk.AtomicIntegerFieldUpdater;

public class AtomicIntegerFieldUpdaterDemo3 {
	public static void main(String[] args) throws InterruptedException {
		AtomicIntegerFieldUpdater<Counter> countFieldUpdater = AtomicIntegerFieldUpdater
				.newUpdater(Counter.class, "count");
		Counter test = new Counter();
		for (int i = 0; i < 10; i++)
			new Thread(() -> {
				for (int j = 0; j < 10000; j++)
					countFieldUpdater.incrementAndGet(test);
			}).start();
		Thread.sleep(3000);
		System.out.println("count = " + test.getCount());
	}

	static class Counter {
		static volatile int count = 0;
		public int getCount() {
			return count;
		}
	}
}

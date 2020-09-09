package com.seaboat.thread;

import com.seaboat.thread.jdk.AtomicInteger;

public class AtomicIntegerFieldUpdaterDemo2 {
	public static void main(String[] args) throws InterruptedException {
		Counter counter = new Counter();
		for (int i = 0; i < 10; i++)
			new Thread(() -> {
				for (int j = 0; j < 10000; j++)
					counter.atomicAdd(1);
			}).start();
		Thread.sleep(3000);
		System.out.println("count = " + counter.getCount());
	}

	static class Counter {
		AtomicInteger count = new AtomicInteger(0);

		public int getCount() {
			return count.get();
		}

		public void atomicAdd(int delta) {
			count.getAndAdd(delta);
		}
	}
}

package com.seaboat.thread;

public class AtomicIntegerFieldUpdaterDemo {
	public static void main(String[] args) throws InterruptedException {
		Counter counter = new Counter();
		for (int i = 0; i < 10; i++)
			new Thread(() -> {
				for (int j = 0; j < 10000; j++)
					counter.count++;
			}).start();
		Thread.sleep(3000);
		System.out.println("count = " + counter.getCount());
	}

	static class Counter {
		volatile int count = 0;
		public int getCount() {
			return count;
		}
	}
}

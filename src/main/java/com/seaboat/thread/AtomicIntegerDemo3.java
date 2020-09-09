package com.seaboat.thread;

public class AtomicIntegerDemo3 {

	static volatile int count = 0;

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 10; i++)
			new Thread(() -> {
				for (int j = 0; j < 10000; j++)
					count++;
			}).start();
		Thread.sleep(3000);
		System.out.println("count = " + count);
	}
}

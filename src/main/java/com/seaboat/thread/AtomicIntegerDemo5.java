package com.seaboat.thread;

import com.seaboat.thread.jdk.AtomicInteger;

public class AtomicIntegerDemo5 {

	static AtomicInteger count = new AtomicInteger(0);

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 10; i++)
			new Thread(() -> {
				for (int j = 0; j < 10000; j++)
					count.getAndAdd(1);
			}).start();
		Thread.sleep(3000);
		System.out.println("count = " + count.get());
	}
}

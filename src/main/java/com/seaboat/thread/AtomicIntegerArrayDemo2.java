package com.seaboat.thread;

import com.seaboat.thread.jdk.AtomicIntegerArray;

public class AtomicIntegerArrayDemo2 {

	static AtomicIntegerArray counts = new AtomicIntegerArray(2);

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 10; i++)
			new Thread(() -> {
				for (int j = 0; j < 10000; j++) {
					counts.incrementAndGet(0);
					counts.incrementAndGet(1);
				}
			}).start();
		Thread.sleep(3000);
		System.out.println("counts[0] = " + counts.get(0));
		System.out.println("counts[1] = " + counts.get(1));
	}
}

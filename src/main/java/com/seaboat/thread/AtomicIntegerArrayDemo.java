package com.seaboat.thread;

public class AtomicIntegerArrayDemo {

	static int[] counts = {0,0};

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 10; i++)
			new Thread(() -> {
				for (int j = 0; j < 10000; j++) {
					counts[0]++;
					counts[1]++;
				}
			}).start();
		Thread.sleep(3000);
		System.out.println("counts[0] = " + counts[0]);
		System.out.println("counts[1] = " + counts[1]);
	}
}

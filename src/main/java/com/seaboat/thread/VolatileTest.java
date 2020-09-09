package com.seaboat.thread;

public class VolatileTest {

	private volatile static int count = 0;

	public static void  increase() {
		count++;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++)
			new Thread(() -> {
				for (int j = 0; j < 10000; j++)
					increase();
			}).start();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}
		System.out.println(count);
	}
}

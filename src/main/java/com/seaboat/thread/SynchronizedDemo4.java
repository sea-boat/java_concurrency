package com.seaboat.thread;

public class SynchronizedDemo4 {

	static int count = 0;

	public synchronized static void add() {
		count++;
	}

	public static void main(String[] args) throws InterruptedException {

		for (int i = 0; i < 10; i++)
			new Thread(() -> {
				for (int j = 0; j < 10000; j++)
					add();
			}).start();

		Thread.sleep(3000);
		System.out.println("count = " + count);
	}
}

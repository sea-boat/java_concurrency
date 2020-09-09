package com.seaboat.thread;

public class SynchronizedDemo6 {

	int count = 0;
	int count2 = 0;

	public void add() {
		synchronized (this) {
			count++;
		}
	}

	public synchronized void add2() {
		count2++;
	}

	public static void main(String[] args) throws InterruptedException {
		SynchronizedDemo6 demo = new SynchronizedDemo6();

		for (int i = 0; i < 10; i++)
			new Thread(() -> {
				for (int j = 0; j < 10000; j++)
					demo.add();
			}).start();

		for (int i = 0; i < 10; i++)
			new Thread(() -> {
				for (int j = 0; j < 10000; j++)
					demo.add2();
			}).start();

		Thread.sleep(3000);
		System.out.println("count = " + demo.count);
		System.out.println("count2 = " + demo.count2);
	}
}

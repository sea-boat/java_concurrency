package com.seaboat.thread;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo3 {
	static CountDownLatch latch = new CountDownLatch(3);

	public static void main(String[] args) {
		Thread thread1 = new Thread(() -> {
			System.out.println("thread1 is waiting for counting down");
			try {
				latch.await();
				System.out.println("thread1 go!");
			} catch (InterruptedException e) {
			}
		});

		Thread thread2 = new Thread(() -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			System.out.println("thread2 countDown!");
			latch.countDown();
			System.out.println("thread2 go!");
		});

		Thread thread3 = new Thread(() -> {
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
			}
			System.out.println("thread3 countDown!");
			latch.countDown();
			System.out.println("thread3 go!");
		});

		thread1.start();
		thread2.start();
		thread3.start();
	}
}

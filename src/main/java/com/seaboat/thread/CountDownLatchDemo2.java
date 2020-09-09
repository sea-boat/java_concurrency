package com.seaboat.thread;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo2 {
	static CountDownLatch latch = new CountDownLatch(2);

	public static void main(String[] args) {
		Thread thread1 = new Thread(() -> {
			System.out.println("thread1 is waiting");
			try {
				latch.await();
				System.out.println("thread1 goes");
			} catch (InterruptedException e) {
			}
		});

		Thread thread2 = new Thread(() -> {
			System.out.println("thread2 is waiting");
			try {
				latch.await();
				System.out.println("thread2 goes");
			} catch (InterruptedException e) {
			}
		});

		thread1.start();
		thread2.start();
		latch.countDown();
		try {
			Thread.currentThread().sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		latch.countDown();
	}
}

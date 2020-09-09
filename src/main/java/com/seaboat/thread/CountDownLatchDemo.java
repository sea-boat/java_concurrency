package com.seaboat.thread;

import com.seaboat.thread.jdk.CountDownLatch;

public class CountDownLatchDemo {
	static CountDownLatch latch = new CountDownLatch(2);

	public static void main(String[] args) {
		Thread thread1 = new Thread(() -> {
			System.out.println("thread1 is waiting");
			try {
				latch.await();
				System.out.println("thread1 go");
			} catch (InterruptedException e) {
			}
		});
		Thread thread2 = new Thread(() -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			System.out.println("thread2 count down");
			latch.countDown();
			System.out.println("thread2 goes");
		});
		Thread thread3 = new Thread(() -> {
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
			}
			System.out.println("thread3 count down");
			latch.countDown();
			System.out.println("thread3 goes");
		});
		thread1.start();
		thread2.start();
		thread3.start();
	}
}

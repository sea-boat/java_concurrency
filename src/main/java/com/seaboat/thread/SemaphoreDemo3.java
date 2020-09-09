package com.seaboat.thread;

import java.util.concurrent.Semaphore;

public class SemaphoreDemo3 {
	static Semaphore unfairSemaphore = new Semaphore(5);
	static Semaphore fairSemaphore = new Semaphore(5, true);

	public static void main(String[] args) {

		for (int i = 0; i < 5; i++) {
			Thread thread = new Thread(() -> {
				try {
					fairSemaphore.acquire();
					Thread.sleep(5000);
					fairSemaphore.release();
				} catch (InterruptedException e) {
				}
			});
			Thread thread2 = new Thread(() -> {
				try {
					unfairSemaphore.acquire();
					Thread.sleep(5000);
					unfairSemaphore.release();
				} catch (InterruptedException e) {
				}
			});
			thread.start();
			thread2.start();
		}
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				System.out.println("fair queue len : " + fairSemaphore.getQueueLength());
				System.out.println("unfair queue len : " + unfairSemaphore.getQueueLength());
			}
		}).start();
		;
	}
}

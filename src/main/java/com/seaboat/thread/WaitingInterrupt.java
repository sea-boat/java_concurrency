package com.seaboat.thread;

public class WaitingInterrupt {
	public static void main(String[] args) {
		Object lock = new Object();
		Thread thread1 = new Thread(() -> {
			try {
				System.out.println("thread1 is running...");
				Thread.currentThread().sleep(200000);
			} catch (InterruptedException e) {
				System.out.println("thread1 has stoped!");
			}
		});
		Thread thread2 = new Thread(() -> {
			try {
				System.out.println("thread2 is running...");
				synchronized (lock) {
					lock.wait();
				}
			} catch (InterruptedException e) {
				System.out.println("thread2 has stoped!");
			}
		});
		thread1.start();
		thread2.start();
		try {
			Thread.currentThread().sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		thread1.interrupt();
		thread2.interrupt();
	}
}

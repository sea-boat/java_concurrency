package com.seaboat.thread;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {
	static ReentrantLock lock = new ReentrantLock();

	public static void main(String[] args) {
		Thread thread1 = new Thread(() -> {
			try {
				lock.lock();
				System.out.println("thread1 got the lock");
				Thread.sleep(2000);
				lock.unlock();
				System.out.println("thread1 release the lock");
			} catch (InterruptedException e) {
			}
		});
		Thread thread2 = new Thread(() -> {
			try {
				lock.lock();
				System.out.println("thread2 got the lock");
				Thread.sleep(2000);
				lock.unlock();
				System.out.println("thread2 release the lock");
			} catch (InterruptedException e) {
			}
		});
		Thread thread3 = new Thread(() -> {
			try {
				lock.lock();
				System.out.println("thread3 got the lock");
				Thread.sleep(2000);
				lock.unlock();
				System.out.println("thread3 release the lock");
			} catch (InterruptedException e) {
			}
		});
		thread1.start();
		thread2.start();
		thread3.start();
	}
}

package com.seaboat.thread;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo2 {
	static ReentrantLock lock = new ReentrantLock();

	public static void main(String[] args) {
		Thread thread1 = new Thread(() -> {
			try {
				lock.lock();
				System.out.println("thread1 got the lock");
				lock.lock();
				System.out.println("thread1 got the lock again");
				System.out.println("lock times : " + lock.getHoldCount());
				Thread.sleep(2000);
				lock.unlock();
				System.out.println("thread1 release the lock");
				System.out.println("lock times : " + lock.getHoldCount());
				lock.unlock();
				System.out.println("thread1 release the lock again");
				System.out.println("lock times : " + lock.getHoldCount());
			} catch (InterruptedException e) {
			}
		});
		thread1.start();
	}
}

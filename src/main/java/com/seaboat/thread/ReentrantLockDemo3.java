package com.seaboat.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo3 {
	static ReentrantLock lock = new ReentrantLock();
	static Condition con = lock.newCondition();

	public static void main(String[] args) throws InterruptedException {
		Thread thread1 = new Thread(() -> {
			try {
				lock.lock();
				System.out.println("thread1 waiting for signal");
				con.await();
				System.out.println("thread1 release the lock");
				lock.unlock();
			} catch (InterruptedException e) {
			}
		});
		Thread thread2 = new Thread(() -> {
			try {
				lock.lock();
				System.out.println("thread2 waiting for signal");
				con.await();
				System.out.println("thread2 release the lock");
				lock.unlock();
			} catch (InterruptedException e) {
			}
		});
		thread1.start();
		thread2.start();
		Thread.sleep(2000);
		lock.lock();
		con.signalAll();
		lock.unlock();
	}
}

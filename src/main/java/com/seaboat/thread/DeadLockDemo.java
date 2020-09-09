package com.seaboat.thread;

public class DeadLockDemo {

	static String lock1 = "lock1";
	static String lock2 = "lock2";

	public static void main(String[] args) {

		new Thread(() -> {
			System.out.println("thread1 trying to get lock1");
			synchronized (lock1) {
				System.out.println("thread1 gets lock1");
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				System.out.println("thread1 trying to get lock2");
				synchronized (lock2) {
					System.out.println("thread1 gets lock2");
				}
			}
		}).start();

		new Thread(() -> {
			System.out.println("thread2 trying to get lock2");
			synchronized (lock2) {
				System.out.println("thread2 gets lock2");
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				System.out.println("thread2 trying to get lock1");
				synchronized (lock1) {
					System.out.println("thread2 gets lock1");
				}
			}
		}).start();

	}

}

package com.seaboat.thread;

import com.seaboat.thread.jdk.LockSupport;

public class LockSupportDemo4 {

	private static String message;

	public static void main(String[] args) throws InterruptedException {
		Thread thread1 = new Thread(() -> {
			LockSupport.park();
			System.out.println(message);
		});

		Thread thread2 = new Thread(() -> {
			message = "thread2 wakes up thread1";
			LockSupport.unpark(thread1);
		});

		thread1.start();
		Thread.sleep(1000);
		thread1.interrupt();
		Thread.sleep(3000);
		thread2.start();
	}
}

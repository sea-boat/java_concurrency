package com.seaboat.thread;

import com.seaboat.thread.jdk.LockSupport;

public class LockSupportDemo1 {
	private static String message;

	public static void main(String[] args) {

		Thread thread1 = new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LockSupport.park();
			System.out.println(message);
		});

		Thread thread2 = new Thread(() -> {
			message = "thread2 wakes up thread1";
			LockSupport.unpark(thread1);
		});

		thread1.start();
		thread2.start();

	}
}

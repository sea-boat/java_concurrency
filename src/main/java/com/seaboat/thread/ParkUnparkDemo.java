package com.seaboat.thread;

import com.seaboat.thread.jdk.LockSupport;

public class ParkUnparkDemo {
	private static String message;

	public static void main(String[] args) {
		Thread thread1 = new Thread(() -> {
			LockSupport.park();
			System.out.println(message);
		});

		Thread thread2 = new Thread(() -> {
			message = "i am thread2";
			LockSupport.unpark(thread1);
		});

		thread1.start();
		thread2.start();
	}
}

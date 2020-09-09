package com.seaboat.thread;

public class ThreadExchangeInfoDemo {
	private static String message1;
	private static String message2;

	public static void main(String[] args) {
		Thread thread1 = new Thread(() -> {
			while (message1 == null || message2 == null) {
				if (message2 == null)
					message2 = "message from thread1";
			}
			System.out.println(Thread.currentThread().getName() + ":" + message1);
		});
		thread1.setName("thread1");
		Thread thread2 = new Thread(() -> {
			while (message2 == null || message1 == null) {
				if (message1 == null)
					message1 = "message from thread2";
			}
			System.out.println(Thread.currentThread().getName() + ":" + message2);
		});
		thread2.setName("thread2");
		thread1.start();
		thread2.start();
	}
}

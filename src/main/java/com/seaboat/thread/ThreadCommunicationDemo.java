package com.seaboat.thread;

public class ThreadCommunicationDemo {
	private static String message;

	public static void main(String[] args) {
		Thread thread1 = new Thread(() -> {
			System.out.println(message);
		});
		Thread thread2 = new Thread(() -> {
			message = "i am thread2";
		});
		thread1.start();
		thread2.start();
	}
}

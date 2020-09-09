package com.seaboat.thread;

public class SuspendResumeDemo {

	private static String message;
	static Thread thread1 = new Thread1();
	static Thread thread2 = new Thread2();

	public static void main(String[] args) {
		thread1.start();
		thread2.start();
	}

	static class Thread1 extends Thread {
		public void run() {
			if (message == null)
				suspend();
			System.out.println(message);
		}
	}

	static class Thread2 extends Thread {
		public void run() {
			message = "i am thread2";
			thread1.resume();
		}
	}

}

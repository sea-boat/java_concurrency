package com.seaboat.thread;

public class VisibilityDemo9 {

	static int x = 0;

	public static void main(String[] args) throws InterruptedException {
		Thread thread1 = new Thread(() -> {
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
			}
			System.out.println("thread1 x = " + x);
		});
		thread1.start();
		x = x + 2;
		Thread.sleep(2000);
		thread1.interrupt();
	}

}
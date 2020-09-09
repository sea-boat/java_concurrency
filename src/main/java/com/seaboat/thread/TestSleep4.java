package com.seaboat.thread;

public class TestSleep4 {

	public static void main(String[] args) throws InterruptedException {
		Thread thread1 = new Thread(() -> {
			System.out.println("thread1 sleeps for 30 seconds.");
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				System.out.println("thread1 is interrupted by thread2.");
			}
		});
		Thread thread2 = new Thread(() -> {
			System.out.println("thread2 interrupts thread1.");
			thread1.interrupt();
		});
		thread1.start();
		Thread.sleep(2000);
		thread2.start();
	}
}
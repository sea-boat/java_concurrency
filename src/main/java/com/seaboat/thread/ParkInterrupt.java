package com.seaboat.thread;

import com.seaboat.thread.jdk.LockSupport;

public class ParkInterrupt {
	public static void main(String[] args) {
		Thread thread1 = new Thread(() -> {
			System.out.println("thread1 is running...");
			LockSupport.park();
		});
		thread1.start();
		try {
			Thread.currentThread().sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		thread1.interrupt();
		System.out.println(thread1.isInterrupted());
	}
}

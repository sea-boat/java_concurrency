package com.seaboat.thread;

public class RunningWaitingInterrupt {
	public static void main(String[] args) {
		Thread thread1 = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				System.out.println("running...");
				try {
					Thread.currentThread().sleep(200);
				} catch (InterruptedException e) {
					System.out.println(Thread.currentThread().isInterrupted());
					Thread.currentThread().interrupt();
					System.out.println("thread1 has stoped!");
				}
			}
		});
		thread1.start();
		try {
			Thread.currentThread().sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		thread1.interrupt();
	}
}

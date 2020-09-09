package com.seaboat.thread;

public class InterruptThreadDemo extends Thread {

	private volatile boolean isInterrupted = false;

	public void customInterrupt() {
		isInterrupted = true;
	}

	public void run() {
		while (!isInterrupted) {
			System.out.println("Thread is running....");
		}
		System.out.println("Interrupt thread.... ");
	}

	public static void main(String args[]) {
		InterruptThreadDemo thread = new InterruptThreadDemo();
		thread.start();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
		thread.customInterrupt();
	}
}

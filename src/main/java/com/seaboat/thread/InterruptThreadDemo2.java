package com.seaboat.thread;

public class InterruptThreadDemo2 extends Thread {

	public void run() {
		while (!Thread.interrupted()) {
			System.out.println("Thread is running....");
		}
		System.out.println("Interrupt thread....");
	}

	public static void main(String args[]) {
		InterruptThreadDemo2 thread = new InterruptThreadDemo2();
		thread.start();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
		thread.interrupt();
	}
}

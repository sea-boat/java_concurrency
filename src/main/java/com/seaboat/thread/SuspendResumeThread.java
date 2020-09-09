package com.seaboat.thread;

public class SuspendResumeThread {
	
	public static void main(String args[]) throws InterruptedException {
		Thread2 thread2 = new Thread2();
		thread2.start();
		Thread.sleep(3000);
		thread2.resume();
	}

	static class Thread2 extends Thread {
		public void run() {
			System.out.println("thread2 is suspended itself");
			suspend();
			System.out.println("thread2 is running again");
		}
	}
}

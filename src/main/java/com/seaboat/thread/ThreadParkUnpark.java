package com.seaboat.thread;

import java.util.concurrent.locks.LockSupport;

public class ThreadParkUnpark {

	public static void main(String[] args) throws InterruptedException {
		MyThread mt = new MyThread();
		mt.start();
		Thread.sleep(100);
		mt.park();
		System.out.println("can you get here?");
		Thread.sleep(3000);
		mt.unpark();
	}

	static class MyThread extends Thread {

		public boolean isPark = false;

		public void run() {
			while (true) {
				if (isPark)
					LockSupport.park();
				System.out.println("running...");
			}
		}

		public void park() {
			this.isPark = true;
		}

		public void unpark() {
			this.isPark = false;
			LockSupport.unpark(this);
		}
	}

}

package com.seaboat.thread;

public class ThreadWaitNotify {

	public static void main(String[] args) throws InterruptedException {
		MyThread mt = new MyThread();
		mt.start();
		Thread.sleep(100);
		mt.suspendThread();
		System.out.println("can you get here?");
		Thread.sleep(3000);
		mt.resumeThread();
	}

	static class MyThread extends Thread {
		public boolean isSuspend = false;

		public void run() {
			while (true) {
				synchronized (this) {
					System.out.println("running...");
					if (isSuspend)
						try {
							wait();
						} catch (InterruptedException e) {
						}
				}
			}
		}

		public void suspendThread() {
			this.isSuspend = true;
		}

		public void resumeThread() {
			synchronized (this) {
				this.isSuspend = false;
				notify();
			}
		}
	}

}

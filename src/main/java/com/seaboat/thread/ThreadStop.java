package com.seaboat.thread;

public class ThreadStop {
	public static Object lock = new Object();

	public static void main(String[] args) {
		Thread mt = new MyThread();
		MyThread2 mt2 = new MyThread2();
		mt.start();
		try {
			Thread.sleep(100);
			mt2.start();
			mt.stop();
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static class MyThread extends Thread {
		public void run() {
			synchronized (lock) {
				while (true) {
					System.out.println("...");
				}
			}
		}
	}

	static class MyThread2 extends Thread {
		public void run() {
			synchronized (lock) {
				System.out.println("get lock!");
			}
		}
	}
}

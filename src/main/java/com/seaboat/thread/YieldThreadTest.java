package com.seaboat.thread;

public class YieldThreadTest {
	public static void main(String[] args) {
		MyThread mt = new MyThread();
		mt.setDaemon(true);
		mt.start();
		for (int i = 0; i < 100; i++) {
			System.out.println("主线程");
		}
	}

	static class MyThread extends Thread {
		public void run() {
			while (true) {
				System.out.println("让出线程CPU时间");
				Thread.currentThread().yield();
			}
		}
	}
}

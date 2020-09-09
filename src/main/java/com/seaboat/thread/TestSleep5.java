package com.seaboat.thread;

public class TestSleep5 {
	public static void main(String[] args) {
		MyThread mt = new MyThread();
		mt.setDaemon(true);
		mt.start();
		for (int i = 0; i < 100; i++) {
			System.out.println("main thread");
		}
	}

	static class MyThread extends Thread {
		public void run() {
			while (true) {
				System.out.println("yield cpu time");
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

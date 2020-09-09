package com.seaboat.thread;

public class JavaThreadTest {

	public static void main(String[] args) {
		System.out.println(Thread.currentThread().getName());
		new MyThread().start();
		new Thread(new MyThread2()).start();
	}

	static class MyThread extends Thread {
		public void run() {
			System.out.println(Thread.currentThread().getName());
		}
	}

	static class MyThread2 implements Runnable {
		public void run() {
			System.out.println(Thread.currentThread().getName());
		}
	}

}

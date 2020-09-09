package com.seaboat.thread;

public class InstructionReorderingDemo {

	static int flag = 0;

	public static void main(String[] args) throws InterruptedException {
		MyThread thread = new MyThread();
		thread.start();
		Thread.sleep(1000);
		flag = 1;
		Thread.sleep(1000);
	}

	public static class MyThread extends Thread {
		public void run() {
			long index = 0;
			while (flag == 0) {
				index++;
			}
			System.out.println("index = " + index);
		}
	}
}

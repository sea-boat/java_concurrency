package com.seaboat.thread;

public class ThreadPriorityTest {

	public static void main(String[] args) {
		Thread t = new MyThread();
		t.setPriority(10);
		t.setName("00");
		Thread t2 = new MyThread();
		t2.setPriority(8);
		t2.setName("11");
		t2.start();
		t.start();
	}

	static class MyThread extends Thread {
		public void run() {
			for (int i = 0; i < 5; i++)
				System.out.println(this.getName());
		}
	}
}

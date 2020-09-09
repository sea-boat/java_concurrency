package com.seaboat.thread;

import com.seaboat.thread.jdk.LockSupport;

public class LockSupportDemo6 {

	public static void main(String[] args) throws InterruptedException {

		Blocker blocker = new Blocker();

		Thread thread1 = new Thread(() -> {
			blocker.doPark();
		});

		Thread thread2 = new Thread(() -> {
			Object o = LockSupport.getBlocker(thread1);
			System.out.println(o);
		});

		thread1.start();
		Thread.sleep(3000);
		thread2.start();
	}

	static class Blocker {
		public void doPark() {
			LockSupport.park(this);
		}

		public String toString() {
			return "我是blocker";
		}
	}
}

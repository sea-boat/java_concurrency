package com.seaboat.thread;

public class StarvationDemo4 {
	private static Object lock = new Object();

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			new Thread(() -> {
				while (true) {
					synchronized (lock) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
						}
					}
				}
			}).start();
		}
		new Thread(() -> {
			while (true) {
				synchronized (lock) {
					lock.notify();
				}
			}
		}).start();
	}
}

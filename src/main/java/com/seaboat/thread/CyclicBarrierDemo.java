package com.seaboat.thread;

import java.util.concurrent.BrokenBarrierException;

import com.seaboat.thread.jdk.CyclicBarrier;

public class CyclicBarrierDemo {
	static CyclicBarrier barrier = new CyclicBarrier(3);

	public static void main(String[] args) {
		Thread thread1 = new Thread(() -> {
			try {
				System.out.println("thread1 is waiting");
				barrier.await();
				System.out.println("thread1 goes");
			} catch (InterruptedException | BrokenBarrierException e) {
			}
		});
		Thread thread2 = new Thread(() -> {
			try {
				Thread.sleep(2000);
				System.out.println("thread2 is waiting");
				barrier.await();
				System.out.println("thread2 goes");
			} catch (InterruptedException | BrokenBarrierException e) {
			}
		});
		Thread thread3 = new Thread(() -> {
			try {
				Thread.sleep(4000);
				System.out.println("thread3 is waiting");
				barrier.await();
				System.out.println("thread3 goes");
			} catch (InterruptedException | BrokenBarrierException e) {
			}
		});
		thread1.start();
		thread2.start();
		thread3.start();
	}
}

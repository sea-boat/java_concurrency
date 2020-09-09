package com.seaboat.thread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;

import com.seaboat.thread.jdk.CyclicBarrier;

public class CountDownLatchVSCyclicBarrier {
	static CyclicBarrier barrier = new CyclicBarrier(2);
	static CountDownLatch latch = new CountDownLatch(2);

	public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
		Thread thread1 = new Thread(() -> {
			try {
				System.out.println("CountDownLatch");
				Thread.sleep(2000);
				latch.countDown();
				latch.countDown();
			} catch (InterruptedException e) {
			}
		});
		thread1.start();
		latch.await();
		Thread thread2 = new Thread(() -> {
			try {
				System.out.println("CyclicBarrier");
				Thread.sleep(2000);
				barrier.await();
				barrier.await();
				System.out.println("can you get here");
			} catch (InterruptedException | BrokenBarrierException e) {
			}
		});
		thread2.start();
	}
}

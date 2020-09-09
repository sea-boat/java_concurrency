package com.seaboat.thread;

import java.util.concurrent.BrokenBarrierException;

import com.seaboat.thread.jdk.CyclicBarrier;

public class CyclicBarrierDemo2 {
	static CyclicBarrier barrier = new CyclicBarrier(2, new Runnable() {
		public void run() {
			System.out.println("我和女朋友都到饭馆了，开始点餐");
		}
	});

	public static void main(String[] args) {
		Thread me = new Thread(() -> {
			try {
				System.out.println("我到达饭馆等女朋友");
				barrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
			}
		});
		Thread girlfriend = new Thread(() -> {
			try {
				System.out.println("女朋友化妆半小时");
				Thread.sleep(30 * 60 * 1000);
				System.out.println("女朋友到达饭馆");
				barrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
			}
		});
		me.start();
		girlfriend.start();
	}
}

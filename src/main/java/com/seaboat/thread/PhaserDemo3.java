package com.seaboat.thread;

import com.seaboat.thread.jdk.Phaser;

public class PhaserDemo3 {
	public static void main(String[] args) throws InterruptedException {
		Phaser phaser = new Phaser();
		System.out.println("比赛即将开始");
		for (int index = 0; index < 5; index++) {
			phaser.register();
			new Thread(() -> {
				try {
					Thread.sleep((long) (Math.random() * 5000));
					System.out.println(Thread.currentThread().getName() + "选手已就位");
					phaser.arriveAndAwaitAdvance();
					//比赛枪响，正式比赛。
					Thread.sleep((long) (Math.random() * 1000));
					System.out.println(Thread.currentThread().getName() + "选手到达终点");
					phaser.arriveAndDeregister();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}).start();
		}
		while (!phaser.isTerminated()) {
		}
		System.out.println("比赛结束");
	}
}

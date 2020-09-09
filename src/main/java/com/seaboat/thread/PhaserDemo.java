package com.seaboat.thread;

import java.io.IOException;

import com.seaboat.thread.jdk.Phaser;

public class PhaserDemo {
	public static void main(String[] args) throws IOException {

		Phaser phaser = new Phaser();
		for (int i = 0; i < 4; i++) {
			phaser.register();
			new Thread(() -> {
				System.out.println("第一阶段" + Thread.currentThread().getName() + ": 执行完任务");
				phaser.arriveAndAwaitAdvance();
				System.out.println("第二阶段" + Thread.currentThread().getName() + ": 执行完任务");
				phaser.arriveAndAwaitAdvance();
				System.out.println("第三阶段" + Thread.currentThread().getName() + ": 执行完任务");
				phaser.arriveAndAwaitAdvance();
			}).start();
		}

	}
}

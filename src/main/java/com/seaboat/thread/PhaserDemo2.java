package com.seaboat.thread;

import java.io.IOException;

import com.seaboat.thread.jdk.Phaser;

public class PhaserDemo2 {
	public static void main(String[] args) throws IOException {
		int phaseNum = 3;
		Phaser phaser = new Phaser() {
			protected boolean onAdvance(int phase, int registeredParties) {
				System.out.println("当前处于第" + phase + "阶段，当前参与线程数为" + registeredParties + "。");
				return phase + 1 >= phaseNum || registeredParties == 0;
			}
		};
		for (int i = 0; i < 4; i++) {
			phaser.register();
			new Thread(() -> {
				while (!phaser.isTerminated()) {
					phaser.arriveAndAwaitAdvance();
					System.out.println(Thread.currentThread().getName() + ": 执行完任务");
				}
			}).start();
		}

	}
}

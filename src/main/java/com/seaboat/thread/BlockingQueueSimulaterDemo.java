package com.seaboat.thread;

import com.seaboat.thread.jdk.BlockingQueueSimulater;

public class BlockingQueueSimulaterDemo {
	public static void main(String[] args) throws InterruptedException {
		BlockingQueueSimulater bq = new BlockingQueueSimulater(10);
		new Thread(() -> {
			try {
				System.out.println("take " + bq.take() + " from queue!");
				Thread.sleep(2000);
				System.out.println("queue size : " + bq.size);
				int size = bq.size;
				for (int i = 0; i < size; i++)
					System.out.print(bq.take() + "|");
			} catch (InterruptedException e) {
			}
		}).start();

		Thread.sleep(2000);

		new Thread(() -> {
			try {
				for (int i = 0; i < 10; i++)
					bq.put("item-" + i);
				System.out.println("10 items is produced!");
			} catch (InterruptedException e) {
			}
		}).start();
	}
}

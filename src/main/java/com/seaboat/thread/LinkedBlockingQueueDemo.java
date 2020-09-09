package com.seaboat.thread;

import com.seaboat.thread.jdk.BlockingQueue;
import com.seaboat.thread.jdk.LinkedBlockingQueue;

public class LinkedBlockingQueueDemo {

	static BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<String>();

	public static void main(String[] args) throws InterruptedException {

		for (int i = 0; i < 5; i++) {
			final int index = i;
			new Thread(() -> {
				System.out.println("thread_" + index + " producing 1000 strings");
				for (int j = 0; j < 1000; j++)
					try {
						blockingQueue.put("thread_" + index + "_" + j);
					} catch (InterruptedException e) {
					}
			}).start();
		}
		Thread.sleep(2000);
		int blockingQueueSize = blockingQueue.size();
		System.out.println("the size of blocking queue is " + blockingQueueSize);
		System.out.println(blockingQueue.take());
		System.out.println(blockingQueue.take());
		System.out.println(blockingQueue.take());
	}
}

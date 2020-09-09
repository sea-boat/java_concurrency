package com.seaboat.thread;

import com.seaboat.thread.jdk.ArrayBlockingQueue;
import com.seaboat.thread.jdk.BlockingQueue;

public class BlockingQueueDemo {
	static BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(10);

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 5; i++) {
			final int index = i;
			new Thread(() -> {
				try {
					System.out.println("producing string" + index);
					blockingQueue.put("string" + index);
				} catch (InterruptedException e) {
				}
			}).start();
		}
		Thread.sleep(2000);
		int blockingQueueSize = blockingQueue.size();
		System.out.println("the size of blocking queue is " + blockingQueueSize);
		for (int i = 0; i < blockingQueueSize; i++)
			System.out.println(blockingQueue.take());
	}
}

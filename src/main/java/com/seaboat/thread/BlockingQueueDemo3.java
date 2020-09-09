package com.seaboat.thread;

import com.seaboat.thread.jdk.ArrayBlockingQueue;
import com.seaboat.thread.jdk.BlockingQueue;

public class BlockingQueueDemo3 {
	static BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(10);

	public static void main(String[] args) throws InterruptedException {
		System.out.println(blockingQueue.take());
	}
}

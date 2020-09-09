package com.seaboat.thread;

import com.seaboat.thread.jdk.BlockingDeque;
import com.seaboat.thread.jdk.LinkedBlockingDeque;

public class LinkedBlockingDequeDemo {
	static BlockingDeque<String> blockingDeque = new LinkedBlockingDeque<String>();

	public static void main(String[] args) throws InterruptedException {
		String[] datas = { "data1", "data2", "data3" };
		String urgency = "urgency_data";
		for (int i = 0; i < 3; i++) {
			final int index = i;
			new Thread(() -> {
				try {
					blockingDeque.put(datas[index]);
				} catch (InterruptedException e) {
				}
			}).start();
			Thread.sleep(500);
		}
		Thread.sleep(2000);
		System.out.println("take " + blockingDeque.take() + " from deque");
		blockingDeque.putFirst("put " + urgency + " into deque");
		System.out.println("take " + blockingDeque.take() + " from deque");
		System.out.println("take " + blockingDeque.take() + " from deque");
		System.out.println("take " + blockingDeque.take() + " from deque");
	}
}

package com.seaboat.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.seaboat.thread.jdk.ExecutorService;
import com.seaboat.thread.jdk.ThreadPoolExecutor;

public class ThreadPoolExecutorDemo {
	
	private static ExecutorService pool;

	public static void main(String[] args) {
		pool = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<Runnable>(1), Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.AbortPolicy());
		for (int i = 0; i < 3; i++) {
			pool.execute(new ThreadTask());
		}
	}
}

class ThreadTask implements Runnable {
	public void run() {
		System.out.println(Thread.currentThread().getName());
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

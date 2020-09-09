package com.seaboat.thread;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

public class ExecutorTest3 {

	public static void main(String[] args) {
		Executor executor = new ThreadPoolExecutor();
		executor.execute(new MyTask());
		executor.execute(new MyTask());
	}

	static class MyTask implements Runnable {
		public void run() {
			System.out.println("executing task...");
		}
	}

	static class ThreadPoolExecutor implements Executor {

		List<Runnable> taskQueue = new LinkedList<Runnable>();
		Thread[] workers = new Thread[10];

		public ThreadPoolExecutor() {
			for (int i = 0; i < workers.length; i++) {
				workers[i] = new Thread(() -> {
					while (true) {
						Runnable task = null;
						synchronized (taskQueue) {
							if (!taskQueue.isEmpty())
								task = taskQueue.remove(0);
						}
						if (task != null)
							task.run();
					}
				});
				workers[i].start();
			}
		}

		public void execute(Runnable r) {
			synchronized (taskQueue) {
				taskQueue.add(r);
			}
		}
	}
}

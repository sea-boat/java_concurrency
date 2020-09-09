package com.seaboat.thread;

import java.util.concurrent.Executor;

public class ExecutorTest2 {

	public static void main(String[] args) {
		Executor executor = new ThreadPerTaskExecutor();
		executor.execute(new MyTask());
	}

	static class MyTask implements Runnable {
		public void run() {
			System.out.println("executing task...");
		}
	}

	static class ThreadPerTaskExecutor implements Executor {
		public void execute(Runnable r) {
			new Thread(r).start();
		}
	}
}

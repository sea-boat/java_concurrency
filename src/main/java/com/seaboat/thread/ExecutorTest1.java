package com.seaboat.thread;

import java.util.concurrent.Executor;

public class ExecutorTest1 {

	public static void main(String[] args) {
		Executor executor = new DirectExecutor();
		executor.execute(new MyTask());
	}

	static class MyTask implements Runnable {
		public void run() {
			System.out.println("executing task...");
		}
	}

	static class DirectExecutor implements Executor {
		public void execute(Runnable r) {
			r.run();
		}
	}
}

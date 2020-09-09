package com.seaboat.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ExecutorServiceTest2 {
	public static void main(String[] args) {
		MyExecutorService executor = new MyExecutorService();
		Future<String> future = executor.submit(new MyTask());
		try {
			String result = future.get();
			System.out.println("receive result : " + result);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		executor.shutdown();
	}

	static class MyTask implements Callable<String> {

		public String call() throws Exception {
			Thread.sleep(3000);
			return "task_result";
		}

	}
}

package com.seaboat.thread;

import com.seaboat.thread.jdk.ExecutorService;

public class ExecutorServiceTest {

	public static void main(String[] args) throws InterruptedException {
		ExecutorService executor = new MyExecutorService();
		for (int i = 0; i < 10; i++)
			executor.execute(new MyTask("task" + i));
		System.out.println("executor isShutdown = " + executor.isShutdown());
		System.out.println("executor isTerminated = " + executor.isTerminated());
		new Thread(() -> {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
			executor.shutdown();
		}).start();
		executor.awaitTermination();
		System.out.println("executor isShutdown = " + executor.isShutdown());
		System.out.println("executor isTerminated = " + executor.isTerminated());
		System.out.println("executor has terminated.");
	}

	static class MyTask implements Runnable {
		String name;

		public MyTask(String name) {
			this.name = name;
		}

		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			System.out.println("executing " + name + " task...");
		}
	}

}

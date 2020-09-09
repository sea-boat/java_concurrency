package com.seaboat.thread;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

import com.seaboat.thread.ExecutorTest2.ThreadPerTaskExecutor;

public class ExecutorTest4 {

	public static void main(String[] args) {
		Executor executor = new SerialExecutor();
		for (int i = 0; i < 5; i++)
			executor.execute(new MyTask("task" + i));
	}

	static class MyTask implements Runnable {
		String name;

		public MyTask(String name) {
			this.name = name;
		}

		public void run() {
			System.out.println("executing " + name + " task...");
		}
	}

	static class SerialExecutor implements Executor {
		final Queue<Runnable> tasks = new ArrayDeque<Runnable>();
		final Executor executor = new ThreadPerTaskExecutor();
		Runnable active;

		public synchronized void execute(final Runnable r) {
			tasks.offer(new Runnable() {
				public void run() {
					try {
						r.run();
					} finally {
						if ((active = tasks.poll()) != null)
							executor.execute(active);
					}
				}
			});
			if (active == null)
				if ((active = tasks.poll()) != null)
					executor.execute(active);
		}
	}
}

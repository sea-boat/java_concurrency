package com.seaboat.thread;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.seaboat.thread.jdk.ExecutorService;

public class MyExecutorService implements ExecutorService {

	volatile boolean isShutdown = false;
	volatile boolean isTerminated = false;
	
	List<Runnable> taskQueue = new LinkedList<Runnable>();
	AtomicInteger count = new AtomicInteger(5);
	Thread[] workers = new Thread[count.get()];
	
	ReentrantLock lock = new ReentrantLock();
	Condition termination = lock.newCondition();

	public MyExecutorService() {
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new Thread(() -> {
				while (true) {
					Runnable task = null;
					synchronized (taskQueue) {
						if (!taskQueue.isEmpty()) {
							task = taskQueue.remove(0);
						}
					}
					if (task != null)
						task.run();
					if (taskQueue.isEmpty() && isShutdown) {
						if (count.decrementAndGet() == 0) {
							lock.lock();
							isTerminated = true;
							termination.signalAll();
							lock.unlock();
						}
						break;
					}
				}
			});
			workers[i].start();
		}
	}

	public void shutdown() {
		isShutdown = true;
	}

	public boolean isShutdown() {
		return isShutdown;
	}

	public boolean isTerminated() {
		return isTerminated;
	}

	public void execute(Runnable r) {
		synchronized (taskQueue) {
			if (!isShutdown)
				taskQueue.add(r);
		}
	}

	public void awaitTermination() throws InterruptedException {
		lock.lock();
		termination.await();
		lock.unlock();
	}

	public <T> Future<T> submit(Callable<T> task) {
		RunnableFuture<T> ftask = new FutureTask<T>(task);
		execute(ftask);
		return ftask;
	}
}

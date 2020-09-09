package com.seaboat.thread.jdk;

import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolExecutor implements ExecutorService {

	private final BlockingQueue<Runnable> workQueue;
	private final ReentrantLock mainLock = new ReentrantLock();
	private final HashSet<Worker> workers = new HashSet<>();
	private final Condition termination = mainLock.newCondition();
	private volatile RejectedExecutionHandler handler = new AbortPolicy();
	private volatile ThreadFactory threadFactory = new DefaultThreadFactory();
	private volatile long keepAliveTime;
	private volatile int corePoolSize;
	private volatile int maximumPoolSize;

	public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
			TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		this.corePoolSize = corePoolSize;
		this.maximumPoolSize = maximumPoolSize;
		this.workQueue = workQueue;
		this.keepAliveTime = unit.toNanos(keepAliveTime);
		this.threadFactory = threadFactory;
		this.handler = handler;
	}

	private final class Worker extends AbstractQueuedSynchronizer implements Runnable {
		final Thread thread;
		Runnable firstTask;

		Worker(Runnable firstTask) {
			setState(-1);
			this.firstTask = firstTask;
			this.thread = threadFactory.newThread(this);
		}

		public void run() {
			Thread wt = Thread.currentThread();
			Runnable task = this.firstTask;
			this.firstTask = null;
			this.unlock();
			boolean completedAbruptly = true;
			try {
				while (task != null || (task = getTask()) != null) {
					this.lock();
					if ((runStateAtLeast(ctl.get(), STOP)
							|| (Thread.interrupted() && runStateAtLeast(ctl.get(), STOP)))
							&& !wt.isInterrupted())
						wt.interrupt();
					try {
						task.run();
					} finally {
						task = null;
						this.unlock();
					}
				}
				completedAbruptly = false;
			} finally {
				processWorkerExit(this, completedAbruptly);
			}
		}

		protected boolean tryAcquire(int unused) {
			if (compareAndSetState(0, 1)) {
				setExclusiveOwnerThread(Thread.currentThread());
				return true;
			}
			return false;
		}

		protected boolean tryRelease(int unused) {
			setExclusiveOwnerThread(null);
			setState(0);
			return true;
		}

		public void lock() {
			acquire(1);
		}

		public boolean tryLock() {
			return tryAcquire(1);
		}

		public void unlock() {
			release(1);
		}
	}

	private Runnable getTask() {
		boolean timedOut = false;
		for (;;) {
			int c = ctl.get();
			if (runStateAtLeast(c, SHUTDOWN) && (runStateAtLeast(c, STOP) || workQueue.isEmpty())) {
				ctl.addAndGet(-1);
				return null;
			}
			int wc = workerCountOf(c);
			boolean timed = wc > corePoolSize;
			if (((timed && timedOut)) && (wc > 1 || workQueue.isEmpty())) {
				if (ctl.compareAndSet(c, c - 1))
					return null;
				continue;
			}
			try {
				Runnable r = timed ? workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS)
						: workQueue.take();
				if (r != null)
					return r;
				timedOut = true;
			} catch (InterruptedException retry) {
				timedOut = false;
			}
		}
	}

	private void processWorkerExit(Worker w, boolean completedAbruptly) {
		if (completedAbruptly)
			ctl.addAndGet(-1);
		final ReentrantLock mainLock = this.mainLock;
		mainLock.lock();
		try {
			workers.remove(w);
		} finally {
			mainLock.unlock();
		}
		tryTerminate();
		int c = ctl.get();
		if (runStateLessThan(c, STOP))
			addWorker(null, false);
	}

	private boolean addWorker(Runnable firstTask, boolean core) {
		retry: for (int c = ctl.get();;) {
			if (runStateAtLeast(c, SHUTDOWN)
					&& (runStateAtLeast(c, STOP) || firstTask != null || workQueue.isEmpty()))
				return false;
			for (;;) {
				if (workerCountOf(c) >= ((core ? corePoolSize : maximumPoolSize) & COUNT_MASK))
					return false;
				if (ctl.compareAndSet(c, c + 1))
					break retry;
				c = ctl.get();
				if (runStateAtLeast(c, SHUTDOWN))
					continue retry;
			}
		}
		boolean workerStarted = false;
		boolean workerAdded = false;
		Worker w = new Worker(firstTask);
		final Thread t = w.thread;
		final ReentrantLock mainLock = this.mainLock;
		mainLock.lock();
		try {
			int c = ctl.get();
			if (isRunning(c) || (runStateLessThan(c, STOP) && firstTask == null)) {
				if (t.isAlive())
					throw new IllegalThreadStateException();
				workers.add(w);
				workerAdded = true;
			}
		} finally {
			mainLock.unlock();
		}
		if (workerAdded) {
			t.start();
			workerStarted = true;
		}
		return workerStarted;
	}

	public void execute(Runnable command) {
		int c = ctl.get();
		if (workerCountOf(c) < corePoolSize) {
			if (addWorker(command, true))
				return;
			c = ctl.get();
		}
		if (isRunning(c) && workQueue.offer(command)) {
			;
		} else if (!addWorker(command, false))
			handler.rejectedExecution(command, this);
	}

	public void shutdown() {
		final ReentrantLock mainLock = this.mainLock;
		mainLock.lock();
		try {
			for (;;) {
				int c = ctl.get();
				if (runStateAtLeast(c, SHUTDOWN)
						|| ctl.compareAndSet(c, ctlOf(SHUTDOWN, workerCountOf(c))))
					break;
			}
			interruptIdleWorkers();
		} finally {
			mainLock.unlock();
		}
		tryTerminate();
	}

	private void interruptIdleWorkers() {
		final ReentrantLock mainLock = this.mainLock;
		mainLock.lock();
		try {
			for (Worker w : workers) {
				Thread t = w.thread;
				if (!t.isInterrupted() && w.tryLock()) {
					try {
						t.interrupt();
					} catch (SecurityException ignore) {
					} finally {
						w.unlock();
					}
				}
			}
		} finally {
			mainLock.unlock();
		}
	}

	final void tryTerminate() {
		for (;;) {
			int c = ctl.get();
			if (isRunning(c) || runStateAtLeast(c, TIDYING)
					|| (runStateLessThan(c, STOP) && !workQueue.isEmpty()))
				return;
			if (workerCountOf(c) != 0) {
				interruptIdleWorkers();
				return;
			}
			final ReentrantLock mainLock = this.mainLock;
			mainLock.lock();
			try {
				if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) {
					try {
						terminated();
					} finally {
						ctl.set(ctlOf(TERMINATED, 0));
						termination.signalAll();
					}
					return;
				}
			} finally {
				mainLock.unlock();
			}
		}
	}

	public void awaitTermination() throws InterruptedException {
		final ReentrantLock mainLock = this.mainLock;
		mainLock.lock();
		try {
			while (runStateLessThan(ctl.get(), TERMINATED)) {
				termination.await();
			}
		} finally {
			mainLock.unlock();
		}
	}

	private static class DefaultThreadFactory implements ThreadFactory {
		private static final AtomicInteger poolNumber = new AtomicInteger(1);
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;

		DefaultThreadFactory() {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
		}

		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}

	public static class AbortPolicy implements RejectedExecutionHandler {
		public AbortPolicy() {
		}

		public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
			throw new RejectedExecutionException(
					"Task " + r.toString() + " rejected from " + e.toString());
		}
	}

	protected void terminated() {
	}

	public <T> Future<T> submit(Callable<T> task) {
		RunnableFuture<T> ftask = new FutureTask<T>(task);
		execute(ftask);
		return ftask;
	}

	private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
	private static final int COUNT_BITS = Integer.SIZE - 3;
	private static final int COUNT_MASK = (1 << COUNT_BITS) - 1;
	private static final int RUNNING = -1 << COUNT_BITS;
	private static final int SHUTDOWN = 0 << COUNT_BITS;
	private static final int STOP = 1 << COUNT_BITS;
	private static final int TIDYING = 2 << COUNT_BITS;
	private static final int TERMINATED = 3 << COUNT_BITS;

	private static int workerCountOf(int c) {
		return c & COUNT_MASK;
	}

	private static int ctlOf(int rs, int wc) {
		return rs | wc;
	}

	private static boolean runStateLessThan(int c, int s) {
		return c < s;
	}

	private static boolean runStateAtLeast(int c, int s) {
		return c >= s;
	}

	private static boolean isRunning(int c) {
		return c < SHUTDOWN;
	}

	public boolean isShutdown() {
		return runStateAtLeast(ctl.get(), SHUTDOWN);
	}

	public boolean isTerminated() {
		return runStateAtLeast(ctl.get(), TERMINATED);
	}

}

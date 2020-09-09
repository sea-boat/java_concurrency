package com.seaboat.thread.jdk;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier {
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition trip = lock.newCondition();
	private final int parties;
	private final Runnable barrierCommand;
	private int count;
	private int flag;

	public CyclicBarrier(int parties) {
		this(parties, null);
	}

	public CyclicBarrier(int parties, Runnable barrierAction) {
		if (parties <= 0)
			throw new IllegalArgumentException();
		this.parties = parties;
		this.count = parties;
		this.barrierCommand = barrierAction;
	}

	private void nextGeneration() {
		trip.signalAll();
		count = parties;
		flag++;
	}

	public int getNumberWaiting() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return parties - count;
		} finally {
			lock.unlock();
		}
	}

	public int await() throws InterruptedException, BrokenBarrierException {
		try {
			return dowait(false, 0L);
		} catch (TimeoutException toe) {
			throw new Error(toe);
		}
	}

	private int dowait(boolean timed, long nanos)
			throws InterruptedException, BrokenBarrierException, TimeoutException {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			int f = flag;
			if (Thread.interrupted())
				throw new InterruptedException();
			int index = --count;
			if (index == 0) {
				final Runnable command = barrierCommand;
				if (command != null)
					command.run();
				nextGeneration();
				return 0;
			}
			for (;;) {
				try {
					if (!timed)
						trip.await();
					else if (nanos > 0L)
						nanos = trip.awaitNanos(nanos);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
				if (f != flag)
					return index;
				if (timed && nanos <= 0L)
					throw new TimeoutException();
			}
		} finally {
			lock.unlock();
		}
	}
}

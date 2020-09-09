package com.seaboat.thread.jdk;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class Semaphore implements java.io.Serializable {
	private final Sync sync;

	public Semaphore(int permits) {
		sync = new NonfairSync(permits);
	}

	public Semaphore(int permits, boolean fair) {
		sync = fair ? new FairSync(permits) : new NonfairSync(permits);
	}

	public void acquire() throws InterruptedException {
		sync.acquireSharedInterruptibly(1);
	}

	public void release() {
		sync.releaseShared(1);
	}

	public void acquire(int permits) throws InterruptedException {
		if (permits < 0)
			throw new IllegalArgumentException();
		sync.acquireSharedInterruptibly(permits);
	}

	public void release(int permits) {
		if (permits < 0)
			throw new IllegalArgumentException();
		sync.releaseShared(permits);
	}

	abstract static class Sync extends AbstractQueuedSynchronizer {

		Sync(int permits) {
			setState(permits);
		}

		final int getPermits() {
			return getState();
		}

		final int nonfairTryAcquireShared(int acquires) {
			for (;;) {
				int available = getState();
				int remaining = available - acquires;
				if (remaining < 0 || compareAndSetState(available, remaining))
					return remaining;
			}
		}

		protected final boolean tryReleaseShared(int releases) {
			for (;;) {
				int current = getState();
				int next = current + releases;
				if (next < current)
					throw new Error("Maximum permit count exceeded");
				if (compareAndSetState(current, next))
					return true;
			}
		}
	}

	static final class NonfairSync extends Sync {
		NonfairSync(int permits) {
			super(permits);
		}

		protected int tryAcquireShared(int acquires) {
			return nonfairTryAcquireShared(acquires);
		}
	}

	static final class FairSync extends Sync {

		FairSync(int permits) {
			super(permits);
		}

		protected int tryAcquireShared(int acquires) {
			for (;;) {
				if (hasQueuedPredecessors())
					return -1;
				int available = getState();
				int remaining = available - acquires;
				if (remaining < 0 || compareAndSetState(available, remaining))
					return remaining;
			}
		}
	}

}

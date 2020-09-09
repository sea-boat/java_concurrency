package com.seaboat.thread.jdk;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class CountDownLatch {
	private final Sync sync;

	public CountDownLatch(int count) {
		if (count < 0)
			throw new IllegalArgumentException("count < 0");
		this.sync = new Sync(count);
	}

	public void await() throws InterruptedException {
		sync.acquireSharedInterruptibly(1);
	}

	public void countDown() {
		sync.releaseShared(1);
	}

	private static final class Sync extends AbstractQueuedSynchronizer {
		Sync(int count) {
			setState(count);
		}

		protected int tryAcquireShared(int acquires) {
			return (getState() == 0) ? 1 : -1;
		}

		protected boolean tryReleaseShared(int releases) {
			for (;;) {
				int c = getState();
				if (c == 0)
					return false;
				int nextc = c - 1;
				if (compareAndSetState(c, nextc))
					return nextc == 0;
			}
		}
	}
}

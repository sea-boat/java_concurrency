package com.seaboat.thread;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class BankServiceWindow {
	private final Sync sync;

	public BankServiceWindow() {
		sync = new Sync();
	}

	private static class Sync extends AbstractQueuedSynchronizer {
		public boolean tryAcquire(int acquires) {
			if (compareAndSetState(0, 1)) {
				setExclusiveOwnerThread(Thread.currentThread());
				return true;
			}
			return false;
		}

		protected boolean tryRelease(int releases) {
			if (getState() == 0 || Thread.currentThread() != this.getExclusiveOwnerThread())
				throw new IllegalMonitorStateException();
			setExclusiveOwnerThread(null);
			setState(0);
			return true;
		}
	}

	public void handle() {
		sync.acquire(1);
	}

	public void unhandle() {
		sync.release(1);
	}

}

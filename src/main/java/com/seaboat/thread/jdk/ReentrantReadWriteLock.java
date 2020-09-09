package com.seaboat.thread.jdk;

import java.util.concurrent.locks.Condition;

public class ReentrantReadWriteLock implements ReadWriteLock, java.io.Serializable {
	private final ReentrantReadWriteLock.ReadLock readerLock;
	private final ReentrantReadWriteLock.WriteLock writerLock;
	final Sync sync;

	public ReentrantReadWriteLock() {
		this(false);
	}

	public ReentrantReadWriteLock(boolean fair) {
		sync = fair ? new FairSync() : new NonfairSync();
		readerLock = new ReadLock(this);
		writerLock = new WriteLock(this);
	}

	public ReentrantReadWriteLock.WriteLock writeLock() {
		return writerLock;
	}

	public ReentrantReadWriteLock.ReadLock readLock() {
		return readerLock;
	}

	abstract static class Sync extends AbstractQueuedSynchronizer {

		static final int SHARED_SHIFT = 16;
		static final int SHARED_UNIT = (1 << SHARED_SHIFT);
		static final int MAX_COUNT = (1 << SHARED_SHIFT) - 1;
		static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

		static int sharedCount(int c) {
			return c >>> SHARED_SHIFT;
		}

		static int exclusiveCount(int c) {
			return c & EXCLUSIVE_MASK;
		}

		Sync() {
			setState(getState());
		}

		protected final boolean tryAcquire(int acquires) {
			Thread current = Thread.currentThread();
			int c = getState();
			int w = exclusiveCount(c);
			if (c != 0) {
				if (w == 0 || current != getExclusiveOwnerThread())
					return false;
				if (w + exclusiveCount(acquires) > MAX_COUNT)
					throw new Error("Maximum lock count exceeded");
				setState(c + acquires);
				return true;
			}
			if (writerShouldBlock() || !compareAndSetState(c, c + acquires))
				return false;
			setExclusiveOwnerThread(current);
			return true;
		}

		protected final boolean tryRelease(int releases) {
			if (!isHeldExclusively())
				throw new IllegalMonitorStateException();
			int nextc = getState() - releases;
			boolean free = exclusiveCount(nextc) == 0;
			if (free)
				setExclusiveOwnerThread(null);
			setState(nextc);
			return free;
		}

		protected final boolean tryReleaseShared(int unused) {
			for (;;) {
				int c = getState();
				int nextc = c - SHARED_UNIT;
				if (compareAndSetState(c, nextc))
					return nextc == 0;
			}
		}

		protected final int tryAcquireShared(int unused) {
			Thread current = Thread.currentThread();
			int c = getState();
			if (exclusiveCount(c) != 0 && getExclusiveOwnerThread() != current)
				return -1;
			int r = sharedCount(c);
			if (!readerShouldBlock() && r < MAX_COUNT && compareAndSetState(c, c + SHARED_UNIT)) {
				return 1;
			}
			return fullTryAcquireShared(current);
		}

		final int fullTryAcquireShared(Thread current) {
			for (;;) {
				int c = getState();
				if (exclusiveCount(c) != 0) {
					if (getExclusiveOwnerThread() != current)
						return -1;
				} else if (readerShouldBlock()) {
					return -1;
				}
				if (sharedCount(c) == MAX_COUNT)
					throw new Error("Maximum lock count exceeded");
				if (compareAndSetState(c, c + SHARED_UNIT))
					return 1;
			}
		}

		final ConditionObject newCondition() {
			return new ConditionObject();
		}

		abstract boolean readerShouldBlock();

		abstract boolean writerShouldBlock();

	}

	static final class FairSync extends Sync {

		final boolean writerShouldBlock() {
			return hasQueuedPredecessors();
		}

		final boolean readerShouldBlock() {
			return hasQueuedPredecessors();
		}
	}

	static final class NonfairSync extends Sync {

		final boolean writerShouldBlock() {
			return false;
		}

		final boolean readerShouldBlock() {
			return apparentlyFirstQueuedIsExclusive();
		}
	}

	public static class ReadLock implements Lock, java.io.Serializable {
		private final Sync sync;

		protected ReadLock(ReentrantReadWriteLock lock) {
			sync = lock.sync;
		}

		public void lock() {
			sync.acquireShared(1);
		}

		public void unlock() {
			sync.releaseShared(1);
		}

		public Condition newCondition() {
			throw new UnsupportedOperationException();
		}
	}

	public static class WriteLock implements Lock, java.io.Serializable {
		private final Sync sync;

		protected WriteLock(ReentrantReadWriteLock lock) {
			sync = lock.sync;
		}

		public void lock() {
			sync.acquire(1);
		}

		public void unlock() {
			sync.release(1);
		}

		public Condition newCondition() {
			return sync.newCondition();
		}
	}

}

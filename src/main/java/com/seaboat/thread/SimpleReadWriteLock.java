package com.seaboat.thread;

public class SimpleReadWriteLock {
	private int readLockNum = 0;
	private int writeLockNum = 0;
	private int writeRequests = 0;

	public synchronized void acquireReadLock() throws InterruptedException {
		while (writeLockNum > 0 || writeRequests > 0) {
			wait();
		}
		readLockNum++;
	}

	public synchronized void releaseReadLock() {
		readLockNum--;
		notifyAll();
	}

	public synchronized void acquireWriteLock() throws InterruptedException {
		writeRequests++;
		while (readLockNum > 0 || writeLockNum > 0) {
			wait();
		}
		writeRequests--;
		writeLockNum++;
	}

	public synchronized void releaseWriteLock() throws InterruptedException {
		writeLockNum--;
		notifyAll();
	}
	
	public static void main(String[] args) throws InterruptedException {
		SimpleReadWriteLock lock = new SimpleReadWriteLock();
		lock.acquireWriteLock();
		lock.acquireWriteLock();
	}
}

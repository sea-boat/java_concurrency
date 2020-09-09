package com.seaboat.thread.jdk;

public interface ReadWriteLock {

	Lock readLock();

	Lock writeLock();
}

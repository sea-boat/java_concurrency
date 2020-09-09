package com.seaboat.thread.jdk;

public abstract class AbstractOwnableSynchronizer {

	protected AbstractOwnableSynchronizer() {
	}

	private transient Thread exclusiveOwnerThread;

	protected final void setExclusiveOwnerThread(Thread thread) {
		exclusiveOwnerThread = thread;
	}

	protected final Thread getExclusiveOwnerThread() {
		return exclusiveOwnerThread;
	}

}

package com.seaboat.thread;

public class ThreadPriority {

	private int priority;

	public static final int MIN_PRIORITY = 1;
	public static final int NORM_PRIORITY = 5;
	public static final int MAX_PRIORITY = 10;

	public final void setPriority(int newPriority) {
		ThreadGroup g;
		checkAccess();
		if (newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY) {
			throw new IllegalArgumentException();
		}
		if ((g = getThreadGroup()) != null) {
			if (newPriority > g.getMaxPriority()) {
				newPriority = g.getMaxPriority();
			}
			setPriority0(priority = newPriority);
		}
	}

	private native void setPriority0(int newPriority);

	public final void checkAccess() {
		SecurityManager security = System.getSecurityManager();
		if (security != null) {
			security.checkAccess(this);
		}
	}

	public final ThreadGroup getThreadGroup() {
		return group;
	}
}

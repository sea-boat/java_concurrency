package com.seaboat.thread.jdk.pseudocode;

import java.util.concurrent.locks.LockSupport;

public class Thread {
	public void interrupt() {...}

	public Boolean isInterrupted() {...}

	public static Boolean interrupted() {...}

	public enum State {
		NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED;
	}
}

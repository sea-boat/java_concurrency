package com.seaboat.thread;

import com.seaboat.thread.jdk.LockSupport;

public class ParkUnparkDemo2 {

	public static void main(String[] args) {
		LockSupport.unpark(Thread.currentThread());
		LockSupport.unpark(Thread.currentThread());
		LockSupport.unpark(Thread.currentThread());
		LockSupport.unpark(Thread.currentThread());
		LockSupport.unpark(Thread.currentThread());
		LockSupport.park();
		LockSupport.park();
	}
}

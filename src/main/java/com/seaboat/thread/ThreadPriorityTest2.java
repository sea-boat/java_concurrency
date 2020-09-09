package com.seaboat.thread;

public class ThreadPriorityTest2 {

	public static void main(String[] args) {

		Thread t = Thread.currentThread();
		System.out.println(t.getPriority());
		Thread.currentThread().setPriority(1);
		System.out.println(t.getPriority());

	}

}

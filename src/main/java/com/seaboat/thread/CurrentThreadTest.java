package com.seaboat.thread;

public class CurrentThreadTest {

	public static void main(String[] args) {
		Thread t = Thread.currentThread();
		System.out.println(t.getName());
	}

}

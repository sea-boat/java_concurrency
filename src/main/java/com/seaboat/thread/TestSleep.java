package com.seaboat.thread;

public class TestSleep {

	public static void main(String[] args) {
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

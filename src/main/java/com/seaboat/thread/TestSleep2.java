package com.seaboat.thread;

public class TestSleep2 {

	public static void main(String[] args) {
		System.out.println("是当前线程睡眠3000ms");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("睡眠结束");
	}

}

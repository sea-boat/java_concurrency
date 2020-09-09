package com.seaboat.thread;

public class CriticalSectionDemo {

	static int a = 0;

	public static int incrementValue() {
		return a++;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++)
			new Thread(() -> {
				for (int j = 0; j < 1000; j++)
					incrementValue();
			}).start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(a);
	}
}

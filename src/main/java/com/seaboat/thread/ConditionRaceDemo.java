package com.seaboat.thread;

import java.util.concurrent.atomic.AtomicInteger;

public class ConditionRaceDemo {

	AtomicInteger a = new AtomicInteger(0);
	AtomicInteger b = new AtomicInteger(0);
	int delta;

	public void update() {
		delta = a.incrementAndGet();
		for (int i = 0; i < 10000; i++)
			;
		b.addAndGet(delta);
	}

	public void print_result() {
		System.out.println(a + "," + b);
	}

	public static void main(String[] args) throws InterruptedException {
		ConditionRaceDemo demo = new ConditionRaceDemo();
		for (int i = 0; i < 10; i++) {
			Thread thread1 = new Thread(() -> {
				demo.update();
			});
			thread1.start();
		}
		Thread.sleep(2000);
		demo.print_result();
	}

}

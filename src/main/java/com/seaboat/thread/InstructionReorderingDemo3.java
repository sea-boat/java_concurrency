package com.seaboat.thread;

import org.junit.jupiter.api.RepeatedTest;

public class InstructionReorderingDemo3 {

	static volatile int x, y, a, b;

	@org.junit.jupiter.api.BeforeEach
	public void init() {
		x = y = a = b = 0;
	}

	@org.junit.jupiter.api.Test
	@RepeatedTest(100000)
	public void test() throws InterruptedException {
		Thread threadA = new Thread(() -> {
			a = 1;
			x = b;
		});
		Thread threadB = new Thread(() -> {
			b = 1;
			y = a;
		});

		threadA.start();
		threadB.start();

		threadA.join();
		threadB.join();

		org.junit.jupiter.api.Assertions.assertFalse(x == 0 && y == 0);
		if (x == 0 && y == 0)
			System.out.println("hahaha");
	}

}

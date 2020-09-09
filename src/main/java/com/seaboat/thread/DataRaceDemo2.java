package com.seaboat.thread;

import java.util.concurrent.atomic.AtomicReference;

public class DataRaceDemo2 {

	Memory mem = new Memory();
	AtomicReference<Memory> aMem = new AtomicReference<Memory>(mem);

	public void update() {
		for (;;) {
			Memory newMem = new Memory();
			newMem.a = aMem.get().a + 1;
			newMem.b = aMem.get().b + 1;
			if (aMem.compareAndSet(aMem.get(), newMem))
				break;
		}
	}

	public void print_result() {
		System.out.println(aMem.get());
	}

	public static void main(String[] args) throws InterruptedException {
		DataRaceDemo demo = new DataRaceDemo();
		Thread thread1 = new Thread(() -> {
			demo.update();
		});
		thread1.start();
		for (int i = 0; i < 50000; i++)
			;
		demo.print_result();
	}

	static class Memory {
		public volatile int a = 0;
		public volatile int b = 0;

		public String toString() {
			return (a + "," + b);
		}
	}
}

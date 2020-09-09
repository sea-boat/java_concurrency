package com.seaboat.thread;

public class DataRaceDemo {

	Memory mem = new Memory();

	public void update() {
		mem.b++;
		mem.a++;
	}

	public void print_result() {
		System.out.println(mem);
	}

	public static void main(String[] args) throws InterruptedException {
		DataRaceDemo demo = new DataRaceDemo();
		Thread thread1 = new Thread(() -> {
			demo.update();
		});
		thread1.start();
		for (int i = 0; i < 5000; i++)
			;
		demo.print_result();
	}

	static class Memory {
		public int a = 0;
		public int b = 0;

		public String toString() {
			return (a + "," + b);
		}
	}
}

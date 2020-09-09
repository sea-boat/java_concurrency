package com.seaboat.thread;


public class AtomicReferenceDemo2 {

	static String[] names = { "tom", "jack", "lucy" };
	static Baoshu bs = new Baoshu();
	static Object lock = new Object();

	public static void main(String[] args) throws InterruptedException {
		for (String name : names) {
			new Thread(() -> {
				synchronized (lock) {
					Baoshu newbs = new Baoshu();
					newbs.name = name;
					newbs.num = bs.num + 1;
					bs = newbs;
					System.out.println(bs);
				}
			}).start();
		}
		Thread.sleep(3000);
	}

	static class Baoshu {
		public String name;
		public int num = 0;
		public String toString() {
			return name + " : " + num;
		}
	}
}

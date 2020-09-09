package com.seaboat.thread;

import com.seaboat.thread.jdk.AtomicReferenceFieldUpdater;

public class AtomicReferenceFieldUpdaterDemo {

	volatile Baoshu bs = new Baoshu();
	static AtomicReferenceFieldUpdater<AtomicReferenceFieldUpdaterDemo, Baoshu> bsUpdater = AtomicReferenceFieldUpdater
			.newUpdater(AtomicReferenceFieldUpdaterDemo.class, Baoshu.class, "bs");

	public static void main(String[] args) throws InterruptedException {
		AtomicReferenceFieldUpdaterDemo demo = new AtomicReferenceFieldUpdaterDemo();
		String[] names = { "tom", "jack", "lucy" };
		for (String name : names) {
			new Thread(() -> {
				for (;;) {
					Baoshu newbs = new Baoshu();
					newbs.name = name;
					newbs.num = bsUpdater.get(demo).num + 1;
					if (bsUpdater.compareAndSet(demo, bsUpdater.get(demo), newbs)) {
						System.out.println(newbs.toString());
						break;
					}
				}
			}).start();
		}
		Thread.sleep(3000);
	}

	static class Baoshu {

		volatile String name;
		volatile int num = 0;

		public String toString() {
			return name + " : " + num;
		}
	}
}

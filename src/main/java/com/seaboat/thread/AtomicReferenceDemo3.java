package com.seaboat.thread;

import com.seaboat.thread.jdk.AtomicReference;

public class AtomicReferenceDemo3 {
	Baoshu bs = new Baoshu();
	//将bs传入AtomicReference则不能再用bs这个引用，而是要用AtomicReference里面的引用，该引用通过get()获取
	AtomicReference<Baoshu> atomicBS = new AtomicReference<Baoshu>(bs);

	public void next(String name) {
		for (;;) {
			Baoshu newbs = new Baoshu();
			newbs.name = name;
			newbs.num = atomicBS.get().num + 1;
			if (atomicBS.compareAndSet(atomicBS.get(), newbs)) {
				System.out.println(newbs.toString());
				break;
			}
		}
	}

	static class Baoshu {
		private String name;
		private int num = 0;
		public String toString() {
			return name + " : " + num;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		AtomicReferenceDemo3 demo = new AtomicReferenceDemo3();
		String[] names = { "tom", "jack", "lucy" };
		for (String name : names) {
			new Thread(() -> {
				demo.next(name);
			}).start();
		}
		Thread.sleep(3000);
	}

}

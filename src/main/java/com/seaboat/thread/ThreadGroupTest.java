package com.seaboat.thread;

public class ThreadGroupTest {
	public static void main(String[] args) {
		System.out.println("A处线程：" + Thread.currentThread().getName() + ", 所属线程："
				+ Thread.currentThread().getThreadGroup().getName() + ", 组中有线程组数量："
				+ Thread.currentThread().getThreadGroup().activeGroupCount());
		ThreadGroup group = new ThreadGroup("新的组");
		System.out.println("B处线程：" + Thread.currentThread().getName() + ", 所属线程："
				+ Thread.currentThread().getThreadGroup().getName() + ", 组中有线程组数量："
				+ Thread.currentThread().getThreadGroup().activeGroupCount());
		ThreadGroup[] tg = new ThreadGroup[Thread.currentThread().getThreadGroup()
				.activeGroupCount()];
		Thread.currentThread().getThreadGroup().enumerate(tg);
		for (int i = 0; i < tg.length; i++)
			System.out.println("第一个线程组名称为：" + tg[i].getName());
	}
}

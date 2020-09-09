package com.seaboat.thread;

import com.seaboat.thread.jdk.ThreadLocal;

public class ThreadLocalDemo {

	static ThreadLocal<String> threadLocal = new ThreadLocal<String>();

	public static void main(String[] args) {

		for (int i = 0; i < 5; i++)
			new Thread(() -> {
				threadLocal.set(Thread.currentThread().getName() + "的变量");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName() + "--->" + threadLocal.get());
			}).start();
	}

}

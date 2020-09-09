package com.seaboat.thread;

import com.seaboat.thread.jdk.CopyOnWriteArrayList;

public class CopyOnWriteArrayListDemo {

	static CopyOnWriteArrayList<String> blackList = new CopyOnWriteArrayList<>(
			new String[] { "name1", "name2", "name3" });

	public static void main(String[] args) throws InterruptedException {

		new Thread(() -> {
			System.out.println("thread1->black list size is " + blackList.size());
			for (int i = 0; i < blackList.size(); i++)
				System.out.print(blackList.get(i) + " ");
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
			}
			System.out.println("\nthread1->black list size is " + blackList.size());
			for (int i = 0; i < blackList.size(); i++)
				System.out.print(blackList.get(i) + " ");
		}).start();

		Thread.sleep(1000);

		new Thread(() -> {
			System.out.println("\nthread2->update the black ");
			blackList.add("name4");
			blackList.add("name5");
			blackList.set(0, "name_updated");
		}).start();

		Thread.sleep(1000);

		new Thread(() -> {
			System.out.println("thread3->black list size is " + blackList.size());
			for (int i = 0; i < blackList.size(); i++)
				System.out.print(blackList.get(i) + " ");
		}).start();

		Thread.sleep(3000);
	}

}

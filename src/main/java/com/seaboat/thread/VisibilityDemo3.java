package com.seaboat.thread;

import java.util.Random;

public class VisibilityDemo3 {
	static int x = 0;
	static int y = 1;

	public static void main(String[] args) throws InterruptedException {
		Thread thread1 = new Thread(() -> {
			while (true) {
				if (x == 2 && y == 3)
					System.out.println("thread1可见变量改变");
//				System.out.println("x,y = " + x + "," + y);//第一种
				new Random().nextInt();//第二种
//				try {
//					Thread.sleep(2000);//第三种
//				} catch (InterruptedException e) {
//				}
			}
		});
		Thread thread2 = new Thread(() -> {
			x = 2;
			y = 3;
		});
		thread1.start();
		Thread.sleep(1000);
		thread2.start();
	}

}
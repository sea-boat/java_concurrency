package com.seaboat.thread;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class StarvationDemo5 {
	static boolean isFair = true;
	private static ReentrantLock lock = new ReentrantLock(isFair);

	public static void main(String[] args) {

		JFrame frame = new JFrame("公平机制锁解决线程饥饿");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		frame.setSize(new Dimension(350, 200));
		for (int i = 0; i < 10; i++) {
			JProgressBar progressBar = new JProgressBar();
			progressBar.setStringPainted(true);
			progressBar.setMinimum(0);
			progressBar.setMaximum(1000);
			frame.add(progressBar);
			new Thread(() -> {
				progressBar.setString(Thread.currentThread().getName());
				int c = 0;
				while (true) {
					if (c >= 1000)
						break;
					lock.lock();
					progressBar.setValue(++c);
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
					}
					lock.unlock();
				}
			}).start();
		}
		frame.setVisible(true);
	}
}

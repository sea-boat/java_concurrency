package com.seaboat.thread;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class StarvationDemo6 {
	private static Object lock = new Object();

	public static void main(String[] args) {
		JFrame frame = new JFrame("公平机制解决线程饥饿");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		frame.setSize(new Dimension(350, 300));
		for (int i = 0; i < 10; i++) {
			JProgressBar progressBar = new JProgressBar();
			progressBar.setStringPainted(true);
			progressBar.setMinimum(0);
			progressBar.setMaximum(100);
			frame.add(progressBar);
			new Thread(() -> {
				int c = 0;
				progressBar.setString(Thread.currentThread().getName());
				while (true) {
					synchronized (lock) {
						try {
							progressBar.setValue(++c);
							lock.wait();
						} catch (InterruptedException e) {
						}
					}
				}
			}).start();
		}
		new Thread(() -> {
			while (true) {
				synchronized (lock) {
					lock.notify();
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}
		}).start();
		frame.setVisible(true);
	}
}

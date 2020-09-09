package com.seaboat.thread;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class StarvationDemo3 {

	static SpinLock spinLock = new SpinLock();

	public static void main(String[] args) {

		JFrame frame = new JFrame("线程自旋饥饿");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		frame.setSize(new Dimension(350, 300));
		for (int i = 0; i < 10; i++) {
			JProgressBar progressBar = new JProgressBar();
			progressBar.setStringPainted(true);
			progressBar.setMinimum(0);
			progressBar.setMaximum(10);
			frame.add(progressBar);
			new Thread(() -> {
				progressBar.setString(Thread.currentThread().getName());
				int c = 0;
				while (true) {
					if (c >= 10)
						break;
					spinLock.lock();
					progressBar.setValue(++c);
					int a = 0;
					for (long l = 0; l < 100000000; l++)
						a++;
					spinLock.unlock();
				}
			}).start();
		}
		frame.setVisible(true);
	}

}

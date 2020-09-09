package com.seaboat.thread;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class StarvationDemo2 {

	static List<Thread> threads = new ArrayList<Thread>();

	public static void main(String[] args) {

		JFrame frame = new JFrame("线程优先级——线程饥饿");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		frame.setSize(new Dimension(350, 200));
		for (int i = 0; i < 10; i++) {
			JProgressBar progressBar = new JProgressBar();
			progressBar.setStringPainted(true);
			progressBar.setMinimum(0);
			progressBar.setMaximum(1000);
			frame.add(progressBar);
			Thread t = new Thread(() -> {
				progressBar.setString(Thread.currentThread().getName());
				int c = 0;
				while (true) {
					if (c >= 1000)
						break;
					progressBar.setValue(++c);
					int a = 0;
					for (long l = 0; l < 10000000; l++)
						a++;
				}
			});
			if (i == 0)
				t.setPriority(1);
			else
				t.setPriority(10);
			threads.add(t);
		}
		frame.setVisible(true);
		for (Thread t : threads)
			t.start();
	}

}

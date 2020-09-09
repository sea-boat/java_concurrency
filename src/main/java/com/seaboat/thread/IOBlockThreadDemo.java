package com.seaboat.thread;

public class IOBlockThreadDemo {
	public static void main(String[] args) {
		IOThread ioThread = new IOThread();
		ioThread.start();
		//主线程任务执行
	}
}

class IOThread extends Thread {
	public void run() {
		while (true) {
			// I/O阻塞
		}
	}
}

package com.seaboat.thread;

public class BankServiceWindowSharedTest {
	public static void main(String[] args) {
		final BankServiceWindowShared bankServiceWindows = new BankServiceWindowShared(2);
		Thread tom = new Thread(() -> {
			bankServiceWindows.handle();
			System.out.println("tom开始办理业务");
			try {
				Thread.currentThread().sleep(5000);
			} catch (InterruptedException e) {
			}
			System.out.println("tom结束办理业务");
			bankServiceWindows.unhandle();
		});
		Thread jim = new Thread(() -> {
			bankServiceWindows.handle();
			System.out.println("jim开始办理业务");
			try {
				Thread.currentThread().sleep(5000);
			} catch (InterruptedException e) {
			}
			System.out.println("jim结束办理业务");
			bankServiceWindows.unhandle();
		});
		Thread jay = new Thread(() -> {
			bankServiceWindows.handle();
			System.out.println("jay开始办理业务");
			try {
				Thread.currentThread().sleep(5000);
			} catch (InterruptedException e) {
			}
			System.out.println("jay结束办理业务");
			bankServiceWindows.unhandle();
		});
		tom.start();
		jim.start();
		jay.start();
	}
}

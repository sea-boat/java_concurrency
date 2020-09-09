package com.seaboat.thread;

public class BankServiceWindowTest {
	public static void main(String[] args) {
		final BankServiceWindow bankServiceWindow = new BankServiceWindow();
		Thread tom = new Thread(() -> {
			bankServiceWindow.handle();
			System.out.println("tom开始办理业务");
			try {
				Thread.currentThread().sleep(5000);
			} catch (InterruptedException e) {
			}
			System.out.println("tom结束办理业务");
			bankServiceWindow.unhandle();
		});
		Thread jim = new Thread(() -> {
			bankServiceWindow.handle();
			System.out.println("jim开始办理业务");
			try {
				Thread.currentThread().sleep(5000);
			} catch (InterruptedException e) {
			}
			System.out.println("jim结束办理业务");
			bankServiceWindow.unhandle();
		});
		Thread jay = new Thread(() -> {
			bankServiceWindow.handle();
			System.out.println("jay开始办理业务");
			try {
				Thread.currentThread().sleep(5000);
			} catch (InterruptedException e) {
			}
			System.out.println("jay结束办理业务");
			bankServiceWindow.unhandle();
		});
		tom.start();
		jim.start();
		jay.start();
	}
}

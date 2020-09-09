package com.seaboat.thread;

import java.util.concurrent.Exchanger;

public class ExchangerDemo {

	public static void main(String[] args) {
		Exchanger<Message> exchanger = new Exchanger<>();
		Thread thread1 = new Thread(() -> {
			try {
				Message goods = new Message("good");
				Message receivedMessage = exchanger.exchange(goods);
				System.out.println(Thread.currentThread().getName() + ":" + receivedMessage.data);
			} catch (InterruptedException e) {
			}
		});
		thread1.setName("tom");
		Thread thread2 = new Thread(() -> {
			try {
				Message money = new Message("money");
				Message receivedMessage = exchanger.exchange(money);
				System.out.println(Thread.currentThread().getName() + ":" + receivedMessage.data);
			} catch (InterruptedException e) {
			}
		});
		thread2.setName("jack");
		thread1.start();
		thread2.start();
	}

	public static class Message {
		public String data;

		public Message(String s) {
			this.data = s;
		}
	}
}

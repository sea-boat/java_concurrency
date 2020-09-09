package com.seaboat.thread;

import java.util.Comparator;

import com.seaboat.thread.jdk.PriorityBlockingQueue;

public class PriorityBlockingQueueDemo2 {
	static class Money {
		private int value;
		Money(int v) {
			this.value = v;
		}
	}

	static class MoneyComparator implements Comparator<Money> {
		public int compare(Money o1, Money o2) {
			return o1.value < o2.value ? -1 : 1;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		PriorityBlockingQueue<Money> queue = new PriorityBlockingQueue<Money>(11,
				new MoneyComparator());
		int[] values = { 1, 100, 2, 20, 10, 5, 50 };
		for (int v : values)
			queue.put(new Money(v));
		for (int i = 0; i < values.length; i++)
			System.out.println(queue.take().value);
	}
}

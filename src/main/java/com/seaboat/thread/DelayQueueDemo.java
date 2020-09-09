package com.seaboat.thread;

import java.util.concurrent.TimeUnit;

import com.seaboat.thread.jdk.DelayQueue;
import com.seaboat.thread.jdk.Delayed;

public class DelayQueueDemo {
	static class Cache implements Delayed {
		private String data;
		private long endTime;

		Cache(String data, long t) {
			this.data = data;
			this.endTime = t;
		}

		public int compareTo(Delayed o) {
			Cache c = (Cache) o;
			return this.endTime - c.endTime > 0 ? 1 : (this.endTime - c.endTime < 0 ? -1 : 0);
		}

		public long getDelay(TimeUnit unit) {
			return unit.convert(endTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		DelayQueue<Cache> queue = new DelayQueue<Cache>();
		String[] datas = { "data1", "data2", "data3" };
		for (int i = 0; i < datas.length; i++) {
			final int index = i;
			new Thread(() -> {
				queue.put(new Cache(datas[index], System.currentTimeMillis() + (index + 1) * 3000));
			}).start();
		}
		for (int i = 0; i < datas.length; i++)
			new Thread(() -> {
				try {
					System.out.println(queue.take().data);
				} catch (InterruptedException e) {
				}
			}).start();
	}
}

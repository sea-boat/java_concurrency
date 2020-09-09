package com.seaboat.thread.jdk;

public class BlockingQueueSimulater {

	Object[] queue;
	public int size;
	private int head = 0;
	private int tail = 0;

	public BlockingQueueSimulater(int maxSize) {
		this.queue = new Object[maxSize];
	}

	public synchronized void put(Object elem) throws InterruptedException {
		while (size == queue.length) {
			wait();
		}
		size++;
		queue[tail] = elem;
		if (tail == queue.length - 1)
			tail = 0;
		else
			tail++;
		if (size == 1) {
			notifyAll();
		}
	}

	public synchronized Object take() throws InterruptedException {
		while (size == 0) {
			wait();
		}
		size--;
		Object elem = queue[head];
		queue[head] = null;
		if (head == queue.length - 1)
			head = 0;
		else
			head++;
		if (size == queue.length - 1) {
			notifyAll();
		}
		return elem;
	}

}

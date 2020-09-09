package com.seaboat.thread;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class TicketLock {
	private static Unsafe unsafe = null;
	private static final long ticketNumOffset;
	private static final long processingNumOffset;
	private volatile int ticketNum = 0;
	private volatile int processingNum = 0;
	static {
		try {
			unsafe = getUnsafeInstance();
			ticketNumOffset = unsafe
					.objectFieldOffset(TicketLock.class.getDeclaredField("ticketNum"));
			processingNumOffset = unsafe
					.objectFieldOffset(TicketLock.class.getDeclaredField("processingNum"));
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	private static Unsafe getUnsafeInstance() throws Exception {
		Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
		theUnsafeInstance.setAccessible(true);
		return (Unsafe) theUnsafeInstance.get(Unsafe.class);
	}

	public int lock() {
		int nowNum;
		for (;;) {
			nowNum = ticketNum;
			if (unsafe.compareAndSwapInt(this, ticketNumOffset, ticketNum, ticketNum + 1)) {
				break;
			}
		}
		while (processingNum != nowNum) {
		}

		return nowNum;
	}

	public void unlock(int ticket) {
		int next = ticket + 1;
		unsafe.compareAndSwapInt(this, processingNumOffset, ticket, next);
	}

	public static void main(String[] args) {
		TicketLock tl = new TicketLock();
		for (int i = 0; i < 20; i++)
			new Thread() {
				public void run() {
					int ticket = tl.lock();
					try {
						Thread.currentThread().sleep(200);
						System.out.println("----------");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					tl.unlock(ticket);
				}
			}.start();
	}
}

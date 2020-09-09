package com.seaboat.thread.jdk;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Exchanger<V> {

	private static final int ASHIFT = 5;
	private static final int MMASK = 0xff;
	private static final int NCPU = Runtime.getRuntime().availableProcessors();
	static final int FULL = (NCPU >= (MMASK << 1)) ? MMASK : NCPU >>> 1;
	private static final Object NULL_ITEM = new Object();
	private static final Object TIMED_OUT = new Object();
	private final Participant participant;
	private volatile Node[] arena;
	private volatile Node slot;
	private volatile int bound;

	static final class Node {
		Object item;
		volatile Object match;
		volatile Thread parked;
	}

	static final class Participant extends ThreadLocal<Node> {
		public Node initialValue() {
			return new Node();
		}
	}

	public Exchanger() {
		participant = new Participant();
	}

	private static final VarHandle BOUND;
	private static final VarHandle SLOT;
	private static final VarHandle MATCH;
	private static final VarHandle AA;
	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			BOUND = l.findVarHandle(Exchanger.class, "bound", int.class);
			SLOT = l.findVarHandle(Exchanger.class, "slot", Node.class);
			MATCH = l.findVarHandle(Node.class, "match", Object.class);
			AA = MethodHandles.arrayElementVarHandle(Node[].class);
		} catch (ReflectiveOperationException e) {
			throw new Error(e);
		}
	}

	public V exchange(V x) throws InterruptedException {
		Object v;
		Node[] a;
		Object item = (x == null) ? NULL_ITEM : x;
		if (((a = arena) != null || (v = slotExchange(item, false, 0L)) == null)
				&& ((Thread.interrupted() || (v = arenaExchange(item, false, 0L)) == null)))
			throw new InterruptedException();
		return (v == NULL_ITEM) ? null : (V) v;
	}

	public V exchange(V x, long timeout, TimeUnit unit)
			throws InterruptedException, TimeoutException {
		Object v;
		Object item = (x == null) ? NULL_ITEM : x;
		long ns = unit.toNanos(timeout);
		if ((arena != null || (v = slotExchange(item, true, ns)) == null)
				&& ((Thread.interrupted() || (v = arenaExchange(item, true, ns)) == null)))
			throw new InterruptedException();
		if (v == TIMED_OUT)
			throw new TimeoutException();
		return (v == NULL_ITEM) ? null : (V) v;
	}

//		private final Object arenaExchange(Object item, boolean timed, long ns) {
//			Node[] a = arena;
//			int alen = a.length;
//			Node p = participant.get();
//			for (int i = 0;;) {
//				int j = (i << ASHIFT) + ((1 << ASHIFT) - 1);
//				if (j < 0 || j >= alen)
//					j = alen - 1;
//				Node q = (Node) AA.getAcquire(a, j);
//				if (q != null && AA.compareAndSet(a, j, q, null)) {
//	
//					//代码块一
//	
//				} else if (i <= FULL && q == null) {
//	
//					//代码块二
//					
//				} else {
//					if (i == FULL)
//						i = 0;
//					else
//						i = i + 1;
//				}
//			}
//		}

	private final Object arenaExchange(Object item, boolean timed, long ns) {
		Node[] a = arena;
		int alen = a.length;
		Node p = participant.get();
		for (int i = 0;;) {
			int j = (i << ASHIFT) + ((1 << ASHIFT) - 1);
			if (j < 0 || j >= alen)
				j = alen - 1;
			Node q = (Node) AA.getAcquire(a, j);
			if (q != null && AA.compareAndSet(a, j, q, null)) {
				Object v = q.item;
				q.match = item;
				Thread w = q.parked;
				if (w != null)
					LockSupport.unpark(w);
				return v;
			} else if (i <= FULL && q == null) {
				p.item = item;
				if (AA.compareAndSet(a, j, null, p)) {
					long end = (timed && i == 0) ? System.nanoTime() + ns : 0L;
					Thread t = Thread.currentThread();
					for (int spins = 1024;;) {
						Object v = p.match;
						if (v != null) {
							MATCH.setRelease(p, null);
							p.item = null;
							return v;
						} else if (spins > 0) {
							spins--;
							Thread.yield();
						} else if (!t.isInterrupted() && i == 0
								&& (!timed || (ns = end - System.nanoTime()) > 0L)) {
							p.parked = t;
							if (AA.getAcquire(a, j) == p) {
								if (ns == 0L)
									LockSupport.park(this);
								else
									LockSupport.parkNanos(this, ns);
							}
							p.parked = null;
						} else if (AA.getAcquire(a, j) == p && AA.compareAndSet(a, j, p, null)) {
							p.item = null;
							i = i >>>= 1;
							if (Thread.interrupted())
								return null;
							if (timed && i == 0 && ns <= 0L)
								return TIMED_OUT;
							break;
						}
					}
				} else
					p.item = null;
			} else {
				if (i == FULL)
					i = 0;
				else
					i = i + 1;
			}
		}
	}

	private final Object slotExchange(Object item, boolean timed, long ns) {
		Node p = participant.get();
		Thread t = Thread.currentThread();
		if (t.isInterrupted())
			return null;
		for (Node q;;) {
			if ((q = slot) != null) {
				if (SLOT.compareAndSet(this, q, null)) {
					Object v = q.item;
					q.match = item;
					Thread w = q.parked;
					if (w != null)
						LockSupport.unpark(w);
					return v;
				}
				if (NCPU > 1 && bound == 0 && BOUND.compareAndSet(this, 0, FULL))
					arena = new Node[(FULL + 2) << ASHIFT];
			} else if (arena != null)
				return null;
			else {
				p.item = item;
				if (SLOT.compareAndSet(this, null, p))
					break;
				p.item = null;
			}
		}
		long end = timed ? System.nanoTime() + ns : 0L;
		Object v;
		while ((v = p.match) == null) {
			if (!t.isInterrupted() && arena == null
					&& (!timed || (ns = end - System.nanoTime()) > 0L)) {
				p.parked = t;
				if (slot == p) {
					if (ns == 0L)
						LockSupport.park(this);
					else
						LockSupport.parkNanos(this, ns);
				}
				p.parked = null;
			} else if (SLOT.compareAndSet(this, p, null)) {
				v = timed && ns <= 0L && !t.isInterrupted() ? TIMED_OUT : null;
				break;
			}
		}
		MATCH.setRelease(p, null);
		p.item = null;
		return v;
	}

	public static void main(String[] args) {
		System.out.println("MMASK=" + MMASK);
		System.out.println("NCPU=" + NCPU);
		System.out.println("FULL=" + FULL);
		System.out.println("MMASK << 1=" + String.valueOf(MMASK << 1));
		System.out.println("(FULL + 2) << ASHIFT=" + String.valueOf((FULL + 2) << ASHIFT));
		System.out.println("(i << ASHIFT) + ((1 << ASHIFT) - 1)="
				+ String.valueOf((0 << ASHIFT) + ((1 << ASHIFT) - 1)));
		System.out.println("(i << ASHIFT) + ((1 << ASHIFT) - 1)="
				+ String.valueOf((1 << ASHIFT) + ((1 << ASHIFT) - 1)));
		System.out.println("(i << ASHIFT) + ((1 << ASHIFT) - 1)="
				+ String.valueOf((2 << ASHIFT) + ((1 << ASHIFT) - 1)));
		int b;
		int m;
		System.out.println("(m = (b = bound) & MMASK) = " + (256 & MMASK));
		int i;
		int a = 0;
		i = a >>>= 1;
		System.out.println(i);
		System.out.println(1 << 10);
	}

}

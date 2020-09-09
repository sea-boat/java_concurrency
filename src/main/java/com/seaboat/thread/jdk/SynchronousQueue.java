package com.seaboat.thread.jdk;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronousQueue<E> implements BlockingQueue<E> {

	abstract static class Transferer<E> {
		abstract E transfer(E e);
	}

	static final int MAX_UNTIMED_SPINS = ((Runtime.getRuntime().availableProcessors() < 2) ? 0 : 32)
			* 16;
	static final long SPIN_FOR_TIMEOUT_THRESHOLD = 1000L;

	static final class TransferStack<E> extends Transferer<E> {
		static final int REQUEST = 0;
		static final int DATA = 1;
		static final int FULFILLING = 2;

		static boolean isFulfilling(int m) {
			return (m & FULFILLING) != 0;
		}

		static final class SNode {
			volatile SNode next;
			volatile SNode match;
			volatile Thread waiter;
			Object item;
			int mode;

			SNode(Object item) {
				this.item = item;
			}

			boolean casNext(SNode cmp, SNode val) {
				return cmp == next && SNEXT.compareAndSet(this, cmp, val);
			}

			boolean tryMatch(SNode s) {
				if (match == null && SMATCH.compareAndSet(this, null, s)) {
					Thread w = waiter;
					if (w != null) {
						waiter = null;
						LockSupport.unpark(w);
					}
					return true;
				}
				return match == s;
			}

			boolean isCancelled() {
				return match == this;
			}

			private static final VarHandle SMATCH;
			private static final VarHandle SNEXT;
			static {
				try {
					MethodHandles.Lookup l = MethodHandles.lookup();
					SMATCH = l.findVarHandle(SNode.class, "match", SNode.class);
					SNEXT = l.findVarHandle(SNode.class, "next", SNode.class);
				} catch (ReflectiveOperationException e) {
					throw new ExceptionInInitializerError(e);
				}
			}
		}

		volatile SNode head;

		boolean casHead(SNode h, SNode nh) {
			return h == head && SHEAD.compareAndSet(this, h, nh);
		}

		static SNode snode(SNode s, Object e, SNode next, int mode) {
			if (s == null)
				s = new SNode(e);
			s.mode = mode;
			s.next = next;
			return s;
		}

		E transfer(E e) {
			SNode s = null;
			int mode = (e == null) ? REQUEST : DATA;
			for (;;) {
				SNode h = head;
				if (h == null || h.mode == mode) {
					if (casHead(h, s = snode(s, e, h, mode))) {
						SNode m = awaitFulfill(s);
						if (m == s) {
							clean(s);
							return null;
						}
						if ((h = head) != null && h.next == s)
							casHead(h, s.next);
						return (E) ((mode == REQUEST) ? m.item : s.item);
					}
				} else if (!isFulfilling(h.mode)) {
					if (h.isCancelled())
						casHead(h, h.next);
					else if (casHead(h, s = snode(s, e, h, FULFILLING | mode))) {
						for (;;) {
							SNode m = s.next;
							if (m == null) {
								casHead(s, null);
								s = null;
								break;
							}
							SNode mn = m.next;
							if (m.tryMatch(s)) {
								casHead(s, mn);
								return (E) ((mode == REQUEST) ? m.item : s.item);
							} else // lost match
								s.casNext(m, mn);
						}
					}
				} else {
					SNode m = h.next;
					if (m == null)
						casHead(h, null);
					else {
						SNode mn = m.next;
						if (m.tryMatch(h))
							casHead(h, mn);
						else
							h.casNext(m, mn);
					}
				}
			}
		}

		SNode awaitFulfill(SNode s) {
			Thread w = Thread.currentThread();
			int spins = shouldSpin(s) ? MAX_UNTIMED_SPINS : 0;
			for (;;) {
				SNode m = s.match;
				if (m != null)
					return m;
				if (spins > 0) {
					Thread.onSpinWait();
					spins = shouldSpin(s) ? (spins - 1) : 0;
				} else if (s.waiter == null)
					s.waiter = w;
				LockSupport.park(this);
			}
		}

		boolean shouldSpin(SNode s) {
			SNode h = head;
			return (h == s || h == null || isFulfilling(h.mode));
		}

		void clean(SNode s) {
			s.item = null;
			s.waiter = null;
			SNode past = s.next;
			if (past != null && past.isCancelled())
				past = past.next;
			SNode p;
			while ((p = head) != null && p != past && p.isCancelled())
				casHead(p, p.next);
			while (p != null && p != past) {
				SNode n = p.next;
				if (n != null && n.isCancelled())
					p.casNext(n, n.next);
				else
					p = n;
			}
		}

		private static final VarHandle SHEAD;
		static {
			try {
				MethodHandles.Lookup l = MethodHandles.lookup();
				SHEAD = l.findVarHandle(TransferStack.class, "head", SNode.class);
			} catch (ReflectiveOperationException e) {
				throw new ExceptionInInitializerError(e);
			}
		}
	}

	static final class TransferQueue<E> extends Transferer<E> {
		static final class QNode {
			volatile QNode next;
			volatile Object item;
			volatile Thread waiter;
			final boolean isData;

			QNode(Object item, boolean isData) {
				this.item = item;
				this.isData = isData;
			}

			boolean casNext(QNode cmp, QNode val) {
				return next == cmp && QNEXT.compareAndSet(this, cmp, val);
			}

			boolean casItem(Object cmp, Object val) {
				return item == cmp && QITEM.compareAndSet(this, cmp, val);
			}

			void tryCancel(Object cmp) {
				QITEM.compareAndSet(this, cmp, this);
			}

			boolean isCancelled() {
				return item == this;
			}

			boolean isOffList() {
				return next == this;
			}

			private static final VarHandle QITEM;
			private static final VarHandle QNEXT;
			static {
				try {
					MethodHandles.Lookup l = MethodHandles.lookup();
					QITEM = l.findVarHandle(QNode.class, "item", Object.class);
					QNEXT = l.findVarHandle(QNode.class, "next", QNode.class);
				} catch (ReflectiveOperationException e) {
					throw new ExceptionInInitializerError(e);
				}
			}
		}

		transient volatile QNode head;
		transient volatile QNode tail;

		TransferQueue() {
			QNode h = new QNode(null, false);
			head = h;
			tail = h;
		}

		void advanceHead(QNode h, QNode nh) {
			if (h == head && QHEAD.compareAndSet(this, h, nh))
				h.next = h; // forget old next
		}

		void advanceTail(QNode t, QNode nt) {
			if (tail == t)
				QTAIL.compareAndSet(this, t, nt);
		}

		E transfer(E e) {
			QNode s = null;
			boolean isData = (e != null);
			for (;;) {
				QNode t = tail;
				QNode h = head;
				if (t == null || h == null)
					continue;

				if (h == t || t.isData == isData) {
					QNode tn = t.next;
					if (t != tail)
						continue;
					if (tn != null) {
						advanceTail(t, tn);
						continue;
					}
					if (s == null)
						s = new QNode(e, isData);
					if (!t.casNext(null, s))
						continue;
					advanceTail(t, s);
					Object x = awaitFulfill(s, e);
					if (x == s) {
						clean(t, s);
						return null;
					}

					if (!s.isOffList()) { // not already unlinked
						advanceHead(t, s); // unlink if head
						if (x != null) // and forget fields
							s.item = s;
						s.waiter = null;
					}
					return (x != null) ? (E) x : e;
				} else { // complementary-mode
					QNode m = h.next; // node to fulfill
					if (t != tail || m == null || h != head)
						continue; // inconsistent read

					Object x = m.item;
					if (isData == (x != null) || // m already fulfilled
							x == m || // m cancelled
							!m.casItem(x, e)) { // lost CAS
						advanceHead(h, m); // dequeue and retry
						continue;
					}
					advanceHead(h, m); // successfully fulfilled
					LockSupport.unpark(m.waiter);
					return (x != null) ? (E) x : e;
				}
			}
		}

		Object awaitFulfill(QNode s, E e) {
			Thread w = Thread.currentThread();
			int spins = (head.next == s) ? MAX_UNTIMED_SPINS : 0;
			for (;;) {
				Object x = s.item;
				if (x != e)
					return x;
				if (spins > 0) {
					--spins;
					Thread.onSpinWait();
				} else if (s.waiter == null)
					s.waiter = w;
				LockSupport.park(this);
			}
		}

		private static final VarHandle QHEAD;
		private static final VarHandle QTAIL;
		private static final VarHandle QCLEANME;
		static {
			try {
				MethodHandles.Lookup l = MethodHandles.lookup();
				QHEAD = l.findVarHandle(TransferQueue.class, "head", QNode.class);
				QTAIL = l.findVarHandle(TransferQueue.class, "tail", QNode.class);
				QCLEANME = l.findVarHandle(TransferQueue.class, "cleanMe", QNode.class);
			} catch (ReflectiveOperationException e) {
				throw new ExceptionInInitializerError(e);
			}
		}
	}

	private transient volatile Transferer<E> transferer;

	public SynchronousQueue() {
		this(false);
	}

	public SynchronousQueue(boolean fair) {
		transferer = fair ? new TransferQueue<E>() : new TransferStack<E>();
	}

	public void put(E e) throws InterruptedException {
		if (e == null)
			throw new NullPointerException();
		if (transferer.transfer(e) == null) {
			Thread.interrupted();
			throw new InterruptedException();
		}
	}

	public E take() throws InterruptedException {
		E e = transferer.transfer(null);
		if (e != null)
			return e;
		Thread.interrupted();
		throw new InterruptedException();
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

}

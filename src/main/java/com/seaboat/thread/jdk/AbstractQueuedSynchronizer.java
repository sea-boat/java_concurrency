package com.seaboat.thread.jdk;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;

public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer {

	private transient volatile Node head;
	private transient volatile Node tail;
	private volatile int state;
	private static final VarHandle STATE;
	private static final VarHandle HEAD;
	private static final VarHandle TAIL;
	static final long SPIN_FOR_TIMEOUT_THRESHOLD = 1000L;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			STATE = l.findVarHandle(AbstractQueuedSynchronizer.class, "state", int.class);
			HEAD = l.findVarHandle(AbstractQueuedSynchronizer.class, "head", Node.class);
			TAIL = l.findVarHandle(AbstractQueuedSynchronizer.class, "tail", Node.class);
		} catch (ReflectiveOperationException e) {
			throw new Error(e);
		}
	}

	protected AbstractQueuedSynchronizer() {
	}

	static final class Node {
		static final Node SHARED = new Node();
		static final Node EXCLUSIVE = null;
		static final int CANCELLED = 1;
		static final int SIGNAL = -1;
		static final int CONDITION = -2;
		static final int PROPAGATE = -3;
		volatile int waitStatus;
		volatile Node prev;
		volatile Node next;
		Node nextWaiter;
		volatile Thread thread;

		private static final VarHandle NEXT;
		private static final VarHandle PREV;
		private static final VarHandle THREAD;
		private static final VarHandle WAITSTATUS;
		static {
			try {
				MethodHandles.Lookup l = MethodHandles.lookup();
				NEXT = l.findVarHandle(Node.class, "next", Node.class);
				PREV = l.findVarHandle(Node.class, "prev", Node.class);
				THREAD = l.findVarHandle(Node.class, "thread", Thread.class);
				WAITSTATUS = l.findVarHandle(Node.class, "waitStatus", int.class);
			} catch (ReflectiveOperationException e) {
				throw new Error(e);
			}
		}

		Node() {
		}

		Node(Node nextWaiter) {
			this.nextWaiter = nextWaiter;
			THREAD.set(this, Thread.currentThread());
		}

		Node(int waitStatus) {
			WAITSTATUS.set(this, waitStatus);
			THREAD.set(this, Thread.currentThread());
		}

		final boolean isShared() {
			return nextWaiter == SHARED;
		}

		final Node predecessor() {
			Node p = prev;
			if (p == null)
				throw new NullPointerException();
			else
				return p;
		}

		final boolean compareAndSetWaitStatus(int expect, int update) {
			return WAITSTATUS.compareAndSet(this, expect, update);
		}

		final boolean compareAndSetNext(Node expect, Node update) {
			return NEXT.compareAndSet(this, expect, update);
		}

		final void setPrevRelaxed(Node p) {
			PREV.set(this, p);
		}

	}

	protected final int getState() {
		return state;
	}

	protected final void setState(int newState) {
		state = newState;
	}

	protected final boolean compareAndSetState(int expect, int update) {
		return STATE.compareAndSet(this, expect, update);
	}

	private final boolean compareAndSetTail(Node expect, Node update) {
		return TAIL.compareAndSet(this, expect, update);
	}

	private void setHead(Node node) {
		head = node;
		node.thread = null;
		node.prev = null;
	}

	private final boolean parkAndCheckInterrupt() {
		LockSupport.park(this);
		return Thread.interrupted();
	}

	public final void acquire(int arg) {
		if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
			Thread.currentThread().interrupt();
	}

	protected boolean tryAcquire(int arg) {
		throw new UnsupportedOperationException();
	}

	private Node addWaiter(Node mode) {
		Node node = new Node(mode);
		for (;;) {
			Node oldTail = tail;
			if (oldTail != null) {
				node.setPrevRelaxed(oldTail);
				if (compareAndSetTail(oldTail, node)) {
					oldTail.next = node;
					return node;
				}
			} else {
				Node h;
				if (HEAD.compareAndSet(this, null, (h = new Node())))
					tail = h;
			}
		}
	}

	final boolean acquireQueued(final Node node, int arg) {
		boolean interrupted = false;
		try {
			for (;;) {
				final Node p = node.predecessor();
				if (p == head && tryAcquire(arg)) {
					setHead(node);
					p.next = null;
					return interrupted;
				}
				if (shouldParkAfterFailedAcquire(p, node))
					interrupted |= parkAndCheckInterrupt();
			}
		} catch (Throwable t) {
			cancelAcquire(node);
			if (interrupted)
				Thread.currentThread().interrupt();
			throw t;
		}
	}

	private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
		int ws = pred.waitStatus;
		if (ws == Node.SIGNAL)
			return true;
		if (ws > 0) {
			do {
				node.prev = pred = pred.prev;
			} while (pred.waitStatus > 0);
			pred.next = node;
		} else {
			pred.compareAndSetWaitStatus(ws, Node.SIGNAL);
		}
		return false;
	}

	private void cancelAcquire(Node node) {
		if (node == null)
			return;
		node.thread = null;
		Node pred = node.prev;
		while (pred.waitStatus > 0)
			node.prev = pred = pred.prev;
		Node predNext = pred.next;
		node.waitStatus = Node.CANCELLED;

		if (node == tail && compareAndSetTail(node, pred)) {
			pred.compareAndSetNext(predNext, null);
		} else {
			int ws;
			if (pred != head
					&& ((ws = pred.waitStatus) == Node.SIGNAL
							|| (ws <= 0 && pred.compareAndSetWaitStatus(ws, Node.SIGNAL)))
					&& pred.thread != null) {
				Node next = node.next;
				if (next != null && next.waitStatus <= 0)
					pred.compareAndSetNext(predNext, next);
			} else {
				unparkSuccessor(node);
			}
			node.next = node;
		}
	}

	private void unparkSuccessor(Node node) {
		int ws = node.waitStatus;
		if (ws < 0)
			node.compareAndSetWaitStatus(ws, 0);
		Node s = node.next;
		if (s == null || s.waitStatus > 0) {
			s = null;
			for (Node p = tail; p != node && p != null; p = p.prev)
				if (p.waitStatus <= 0)
					s = p;
		}
		if (s != null)
			LockSupport.unpark(s.thread);
	}

	public final boolean release(int arg) {
		if (tryRelease(arg)) {
			Node h = head;
			if (h != null && h.waitStatus != 0)
				unparkSuccessor(h);
			return true;
		}
		return false;
	}

	protected boolean tryRelease(int arg) {
		throw new UnsupportedOperationException();
	}

	public final void acquireInterruptibly(int arg) throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		if (!tryAcquire(arg))
			doAcquireInterruptibly(arg);
	}

	private void doAcquireInterruptibly(int arg) throws InterruptedException {
		final Node node = addWaiter(Node.EXCLUSIVE);
		try {
			for (;;) {
				final Node p = node.predecessor();
				if (p == head && tryAcquire(arg)) {
					setHead(node);
					p.next = null;
					return;
				}
				if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
					throw new InterruptedException();
			}
		} catch (Throwable t) {
			cancelAcquire(node);
			throw t;
		}
	}

	public final boolean tryAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		return tryAcquire(arg) || doAcquireNanos(arg, nanosTimeout);
	}

	private boolean doAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
		if (nanosTimeout <= 0L)
			return false;
		final long deadline = System.nanoTime() + nanosTimeout;
		final Node node = addWaiter(Node.EXCLUSIVE);
		try {
			for (;;) {
				final Node p = node.predecessor();
				if (p == head && tryAcquire(arg)) {
					setHead(node);
					p.next = null;
					return true;
				}
				nanosTimeout = deadline - System.nanoTime();
				if (nanosTimeout <= 0L) {
					cancelAcquire(node);
					return false;
				}
				if (shouldParkAfterFailedAcquire(p, node)
						&& nanosTimeout > SPIN_FOR_TIMEOUT_THRESHOLD)
					LockSupport.parkNanos(this, nanosTimeout);
				if (Thread.interrupted())
					throw new InterruptedException();
			}
		} catch (Throwable t) {
			cancelAcquire(node);
			throw t;
		}
	}

	public final void acquireShared(int arg) {
		if (tryAcquireShared(arg) < 0)
			doAcquireShared(arg);
	}

	protected int tryAcquireShared(int arg) {
		throw new UnsupportedOperationException();
	}

	private void doAcquireShared(int arg) {
		final Node node = addWaiter(Node.SHARED);
		boolean interrupted = false;
		try {
			for (;;) {
				final Node p = node.predecessor();
				if (p == head) {
					int r = tryAcquireShared(arg);
					if (r >= 0) {
						setHeadAndPropagate(node, r);
						p.next = null;
						return;
					}
				}
				if (shouldParkAfterFailedAcquire(p, node))
					interrupted |= parkAndCheckInterrupt();
			}
		} catch (Throwable t) {
			cancelAcquire(node);
			throw t;
		} finally {
			if (interrupted)
				Thread.currentThread().interrupt();
		}
	}

	private void setHeadAndPropagate(Node node, int propagate) {
		Node h = head;
		setHead(node);
		if (propagate > 0 || h == null || h.waitStatus < 0 || (h = head) == null
				|| h.waitStatus < 0) {
			Node s = node.next;
			if (s == null || s.isShared())
				doReleaseShared();
		}
	}

	public final boolean releaseShared(int arg) {
		if (tryReleaseShared(arg)) {
			doReleaseShared();
			return true;
		}
		return false;
	}

	protected boolean tryReleaseShared(int arg) {
		throw new UnsupportedOperationException();
	}

	private void doReleaseShared() {
		for (;;) {
			Node h = head;
			if (h != null && h != tail) {
				int ws = h.waitStatus;
				if (ws == Node.SIGNAL) {
					if (!h.compareAndSetWaitStatus(Node.SIGNAL, 0))
						continue;
					unparkSuccessor(h);
				} else if (ws == 0 && !h.compareAndSetWaitStatus(0, Node.PROPAGATE))
					continue;
			}
			if (h == head)
				break;
		}
	}

	protected boolean isHeldExclusively() {
		throw new UnsupportedOperationException();
	}

	public final void acquireSharedInterruptibly(int arg) throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		if (tryAcquireShared(arg) < 0)
			doAcquireSharedInterruptibly(arg);
	}

	private void doAcquireSharedInterruptibly(int arg) throws InterruptedException {
		final Node node = addWaiter(Node.SHARED);
		try {
			for (;;) {
				final Node p = node.predecessor();
				if (p == head) {
					int r = tryAcquireShared(arg);
					if (r >= 0) {
						setHeadAndPropagate(node, r);
						p.next = null;
						return;
					}
				}
				if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
					throw new InterruptedException();
			}
		} catch (Throwable t) {
			cancelAcquire(node);
			throw t;
		}
	}

	public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout)
			throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		return tryAcquireShared(arg) >= 0 || doAcquireSharedNanos(arg, nanosTimeout);
	}

	private boolean doAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException {
		if (nanosTimeout <= 0L)
			return false;
		final long deadline = System.nanoTime() + nanosTimeout;
		final Node node = addWaiter(Node.SHARED);
		try {
			for (;;) {
				final Node p = node.predecessor();
				if (p == head) {
					int r = tryAcquireShared(arg);
					if (r >= 0) {
						setHeadAndPropagate(node, r);
						p.next = null;
						return true;
					}
				}
				nanosTimeout = deadline - System.nanoTime();
				if (nanosTimeout <= 0L) {
					cancelAcquire(node);
					return false;
				}
				if (shouldParkAfterFailedAcquire(p, node)
						&& nanosTimeout > SPIN_FOR_TIMEOUT_THRESHOLD)
					LockSupport.parkNanos(this, nanosTimeout);
				if (Thread.interrupted())
					throw new InterruptedException();
			}
		} catch (Throwable t) {
			cancelAcquire(node);
			throw t;
		}
	}

	public class ConditionObject implements Condition, java.io.Serializable {

		private transient Node firstWaiter;
		private transient Node lastWaiter;
		private static final int REINTERRUPT = 1;
		private static final int THROW_IE = -1;

		public ConditionObject() {
		}

		private Node addConditionWaiter() {
			if (!isHeldExclusively())
				throw new IllegalMonitorStateException();
			Node t = lastWaiter;
			if (t != null && t.waitStatus != Node.CONDITION) {
				unlinkCancelledWaiters();
				t = lastWaiter;
			}
			Node node = new Node(Node.CONDITION);
			if (t == null)
				firstWaiter = node;
			else
				t.nextWaiter = node;
			lastWaiter = node;
			return node;
		}

		private void doSignal(Node first) {
			do {
				if ((firstWaiter = first.nextWaiter) == null)
					lastWaiter = null;
				first.nextWaiter = null;
			} while (!transferForSignal(first) && (first = firstWaiter) != null);
		}

		private void doSignalAll(Node first) {
			lastWaiter = firstWaiter = null;
			do {
				Node next = first.nextWaiter;
				first.nextWaiter = null;
				transferForSignal(first);
				first = next;
			} while (first != null);
		}

		private void unlinkCancelledWaiters() {
			Node t = firstWaiter;
			Node trail = null;
			while (t != null) {
				Node next = t.nextWaiter;
				if (t.waitStatus != Node.CONDITION) {
					t.nextWaiter = null;
					if (trail == null)
						firstWaiter = next;
					else
						trail.nextWaiter = next;
					if (next == null)
						lastWaiter = trail;
				} else
					trail = t;
				t = next;
			}
		}

		public final void signal() {
			if (!isHeldExclusively())
				throw new IllegalMonitorStateException();
			Node first = firstWaiter;
			if (first != null)
				doSignal(first);
		}

		public final void signalAll() {
			if (!isHeldExclusively())
				throw new IllegalMonitorStateException();
			Node first = firstWaiter;
			if (first != null)
				doSignalAll(first);
		}

		public final void awaitUninterruptibly() {
			Node node = addConditionWaiter();
			int savedState = fullyRelease(node);
			boolean interrupted = false;
			while (!isOnSyncQueue(node)) {
				LockSupport.park(this);
				if (Thread.interrupted())
					interrupted = true;
			}
			if (acquireQueued(node, savedState) || interrupted)
				Thread.currentThread().interrupt();
		}

		private int checkInterruptWhileWaiting(Node node) {
			return Thread.interrupted()
					? (transferAfterCancelledWait(node) ? THROW_IE : REINTERRUPT)
					: 0;
		}

		private void reportInterruptAfterWait(int interruptMode) throws InterruptedException {
			if (interruptMode == THROW_IE)
				throw new InterruptedException();
			else if (interruptMode == REINTERRUPT)
				Thread.currentThread().interrupt();
		}

		public final void await() throws InterruptedException {
			if (Thread.interrupted())
				throw new InterruptedException();
			Node node = addConditionWaiter();
			int savedState = fullyRelease(node);
			while (!isOnSyncQueue(node)) {
				LockSupport.park(this);
			}
			acquireQueued(node, savedState);
			if (node.nextWaiter != null)
				unlinkCancelledWaiters();
		}

		public final long awaitNanos(long nanosTimeout) throws InterruptedException {
			if (Thread.interrupted())
				throw new InterruptedException();
			final long deadline = System.nanoTime() + nanosTimeout;
			long initialNanos = nanosTimeout;
			Node node = addConditionWaiter();
			int savedState = fullyRelease(node);
			int interruptMode = 0;
			while (!isOnSyncQueue(node)) {
				if (nanosTimeout <= 0L) {
					transferAfterCancelledWait(node);
					break;
				}
				if (nanosTimeout > SPIN_FOR_TIMEOUT_THRESHOLD)
					LockSupport.parkNanos(this, nanosTimeout);
				if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
					break;
				nanosTimeout = deadline - System.nanoTime();
			}
			if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
				interruptMode = REINTERRUPT;
			if (node.nextWaiter != null)
				unlinkCancelledWaiters();
			if (interruptMode != 0)
				reportInterruptAfterWait(interruptMode);
			long remaining = deadline - System.nanoTime(); // avoid overflow
			return (remaining <= initialNanos) ? remaining : Long.MIN_VALUE;
		}

		public final boolean awaitUntil(Date deadline) throws InterruptedException {
			long abstime = deadline.getTime();
			if (Thread.interrupted())
				throw new InterruptedException();
			Node node = addConditionWaiter();
			int savedState = fullyRelease(node);
			boolean timedout = false;
			int interruptMode = 0;
			while (!isOnSyncQueue(node)) {
				if (System.currentTimeMillis() >= abstime) {
					timedout = transferAfterCancelledWait(node);
					break;
				}
				LockSupport.parkUntil(this, abstime);
				if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
					break;
			}
			if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
				interruptMode = REINTERRUPT;
			if (node.nextWaiter != null)
				unlinkCancelledWaiters();
			if (interruptMode != 0)
				reportInterruptAfterWait(interruptMode);
			return !timedout;
		}

		public final boolean await(long time, TimeUnit unit) throws InterruptedException {
			long nanosTimeout = unit.toNanos(time);
			if (Thread.interrupted())
				throw new InterruptedException();
			final long deadline = System.nanoTime() + nanosTimeout;
			Node node = addConditionWaiter();
			int savedState = fullyRelease(node);
			boolean timedout = false;
			int interruptMode = 0;
			while (!isOnSyncQueue(node)) {
				if (nanosTimeout <= 0L) {
					timedout = transferAfterCancelledWait(node);
					break;
				}
				if (nanosTimeout > SPIN_FOR_TIMEOUT_THRESHOLD)
					LockSupport.parkNanos(this, nanosTimeout);
				if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
					break;
				nanosTimeout = deadline - System.nanoTime();
			}
			if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
				interruptMode = REINTERRUPT;
			if (node.nextWaiter != null)
				unlinkCancelledWaiters();
			if (interruptMode != 0)
				reportInterruptAfterWait(interruptMode);
			return !timedout;
		}

	}

	final boolean isOnSyncQueue(Node node) {
		if (node.waitStatus == Node.CONDITION || node.prev == null)
			return false;
		if (node.next != null)
			return true;
		return findNodeFromTail(node);
	}

	private boolean findNodeFromTail(Node node) {
		for (Node p = tail;;) {
			if (p == node)
				return true;
			if (p == null)
				return false;
			p = p.prev;
		}
	}

	private Node enq(Node node) {
		for (;;) {
			Node oldTail = tail;
			if (oldTail != null) {
				node.setPrevRelaxed(oldTail);
				if (compareAndSetTail(oldTail, node)) {
					oldTail.next = node;
					return oldTail;
				}
			} else {
				Node h;
				if (HEAD.compareAndSet(this, null, (h = new Node())))
					tail = h;
			}
		}
	}

	final boolean transferForSignal(Node node) {
		if (!node.compareAndSetWaitStatus(Node.CONDITION, 0))
			return false;
		Node p = enq(node);
		int ws = p.waitStatus;
		if (ws > 0 || !p.compareAndSetWaitStatus(ws, Node.SIGNAL))
			LockSupport.unpark(node.thread);
		return true;
	}

	final boolean transferAfterCancelledWait(Node node) {
		if (node.compareAndSetWaitStatus(Node.CONDITION, 0)) {
			enq(node);
			return true;
		}
		while (!isOnSyncQueue(node))
			Thread.yield();
		return false;
	}

	final int fullyRelease(Node node) {
		try {
			int savedState = getState();
			if (release(savedState))
				return savedState;
			throw new IllegalMonitorStateException();
		} catch (Throwable t) {
			node.waitStatus = Node.CANCELLED;
			throw t;
		}
	}

	public final boolean hasQueuedPredecessors() {
		Node h, s;
		if ((h = head) != null) {
			if ((s = h.next) == null || s.waitStatus > 0) {
				s = null; // traverse in case of concurrent cancellation
				for (Node p = tail; p != h && p != null; p = p.prev) {
					if (p.waitStatus <= 0)
						s = p;
				}
			}
			if (s != null && s.thread != Thread.currentThread())
				return true;
		}
		return false;
	}

	final boolean apparentlyFirstQueuedIsExclusive() {
		Node h, s;
		return (h = head) != null && (s = h.next) != null && !s.isShared() && s.thread != null;
	}
}

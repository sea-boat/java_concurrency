package com.seaboat.thread.jdk;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class Phaser {
	private static final VarHandle STATE;
	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			STATE = l.findVarHandle(Phaser.class, "state", long.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	private volatile long state;

	private static final int MAX_PHASE = Integer.MAX_VALUE;
	private static final int PARTIES_SHIFT = 16;
	private static final int PHASE_SHIFT = 32;
	private static final int UNARRIVED_MASK = 0xffff;
	private static final long PARTIES_MASK = 0xffff0000L;
	private static final long TERMINATION_BIT = 1L << 63;
	private static final int EMPTY = 1;

	public Phaser() {
		this(0);
	}

	public Phaser(int parties) {
		int phase = 0;
		this.state = (parties == 0) ? (long) EMPTY
				: ((long) phase << PHASE_SHIFT) | ((long) parties << PARTIES_SHIFT)
						| ((long) parties);
	}

	public int register() {
		long adjust = ((long) 1 << PARTIES_SHIFT) | 1;
		int phase;
		for (;;) {
			int counts = (int) state;
			int parties = counts >>> PARTIES_SHIFT;
			int unarrived = counts & UNARRIVED_MASK;
			phase = (int) (state >>> PHASE_SHIFT);
			if (counts != EMPTY) {
				if (unarrived == 0)
					this.internalAwaitAdvance(phase);
				else if (STATE.compareAndSet(this, state, state + adjust))
					break;
			} else {
				long next = ((long) phase << PHASE_SHIFT) | adjust;
				if (STATE.compareAndSet(this, state, next))
					break;
			}
		}
		return phase;
	}

	private int internalAwaitAdvance(int phase) {
		int p;
		while ((p = (int) (state >>> PHASE_SHIFT)) == phase)
			Thread.onSpinWait();
		return p;
	}

	public int arriveAndAwaitAdvance() {
		for (;;) {
			long s = state;
			int phase = (int) (s >>> PHASE_SHIFT);
			int counts = (int) s;
			int unarrived = (counts == EMPTY) ? 0 : (counts & UNARRIVED_MASK);
			if (STATE.compareAndSet(this, s, s -= 1)) {
				if (unarrived > 1)
					return this.internalAwaitAdvance(phase);
				long n = s & PARTIES_MASK;
				int nextUnarrived = (int) n >>> PARTIES_SHIFT;
				if (onAdvance(phase, nextUnarrived))
					n |= TERMINATION_BIT;
				else if (nextUnarrived == 0)
					n |= EMPTY;
				else
					n |= nextUnarrived;
				int nextPhase = (phase + 1) & MAX_PHASE;
				n |= (long) nextPhase << PHASE_SHIFT;
				if (!STATE.compareAndSet(this, s, n))
					return (int) (state >>> PHASE_SHIFT);
				return nextPhase;
			}
		}
	}

	public int arriveAndDeregister() {
		for (;;) {
			long s = state;
			int phase = (int) (s >>> PHASE_SHIFT);
			if (phase < 0)
				return phase;
			int counts = (int) s;
			int unarrived = (counts == EMPTY) ? 0 : (counts & UNARRIVED_MASK);
			if (STATE.compareAndSet(this, s, s -= (1 | 1 << PARTIES_SHIFT))) {
				if (unarrived == 1) {
					long n = s & PARTIES_MASK;
					int nextUnarrived = (int) n >>> PARTIES_SHIFT;
					if (onAdvance(phase, nextUnarrived))
						n |= TERMINATION_BIT;
					else if (nextUnarrived == 0)
						n |= EMPTY;
					else
						n |= nextUnarrived;
					int nextPhase = (phase + 1) & MAX_PHASE;
					n |= (long) nextPhase << PHASE_SHIFT;
					boolean result = STATE.compareAndSet(this, s, n);
				}
				return phase;
			}
		}
	}

	protected boolean onAdvance(int phase, int registeredParties) {
		return registeredParties == 0;
	}

	public boolean isTerminated() {
		return this.state < 0L;
	}

}

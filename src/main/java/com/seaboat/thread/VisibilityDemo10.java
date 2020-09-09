package com.seaboat.thread;

public class VisibilityDemo10 {

	int x = 0;

	private void updateX(int newX) {
		this.x = newX;
	}

	protected void finalize() throws Throwable {
		System.out.println("finalize方法  x = " + x);
	}

	public static void main(String[] args) {
		VisibilityDemo10 demo = new VisibilityDemo10();
		demo.updateX(4);
		demo = null;
		System.gc();
	}

}
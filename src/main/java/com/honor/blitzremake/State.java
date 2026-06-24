package com.honor.blitzremake;

public class State {

	public static final int MENU = 0x0;
	public static final int GAME = 0x1;
	public static final int ENDING = 0x2;

	private static int current_state = MENU;

	public static int getState() {
		return current_state;
	}

	public static void setState(int state) {
		current_state = state;
	}

}

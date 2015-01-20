package com.sdp.group13;
public enum Movement {
	STOP(0), START(1), UP(2), DOWN(3), RIGHT(4), LEFT(5), KICK(6);

	private final int value;

	Movement(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	public static int getValue(String action) {
		System.out.println(action);
		switch (action) {
		case "Start":
			return START.value();
		case "Stop":
			return STOP.value();
		case "Up":
			return UP.value();
		case "Down":
			return DOWN.value();
		case "Right":
			return RIGHT.value();
		case "Left":
			return LEFT.value();
		case "Kick":
			return KICK.value();
		default:
			return -1;
		}
	}
}

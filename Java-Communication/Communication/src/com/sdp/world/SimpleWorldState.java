package com.sdp.world;

public class SimpleWorldState {
	public static enum Operation {
		RIGHT, LEFT, SHORT_RIGHT, SHORT_LEFT, FORWARD, BACKWARD, CATCH, KICK, NONE
	}

	public static Operation previousOperation = Operation.NONE;

	public static boolean doesOurRobotHaveBall = false;
}
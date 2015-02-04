package com.sdp;

public class Constants {
	public static boolean areWeShootingRight = false;

	public static enum Strategy {
		ATTACK, DEFEND
	}

	public static Strategy currentStrategy;

	public static int KICK_TIME = 300;

	public static String PORT_NAME = "/dev/ttyACM0";
}

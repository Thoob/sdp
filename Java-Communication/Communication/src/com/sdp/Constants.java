package com.sdp;

public class Constants {
	public static boolean areWeShootingRight = false;

	public static enum Strategy {
		ATTACK, DEFEND, NONE
	}

	public static Strategy currentStrategy = Strategy.ATTACK;

	public static int KICK_TIME = 300;

	public static String PORT_NAME = "/dev/ttyACM0";
}

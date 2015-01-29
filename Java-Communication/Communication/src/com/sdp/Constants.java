package com.sdp;

public class Constants {
	public static enum Strategy{
		ATTACK, DEFEND
	}
	public static Strategy currentStrategy;
	
	public static int KICK_TIME = 300;
	public static int MOVE_FORWARD_10_TIME = 1000;
	public static int MOVE_FORWARD_50_TIME = 5000;
	public static int MOVE_BACKWARD_10_TIME = 1000;
}

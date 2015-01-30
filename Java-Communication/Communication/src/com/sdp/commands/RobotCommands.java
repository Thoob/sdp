package com.sdp.commands;

import com.sdp.RobotCommunication;

public class RobotCommands {
	
	public void changeDirection(float curDir, float toDir){
		while(curDir<toDir){
			RobotCommunication.getInstance().holdLeft();
			curDir++;
		}
	}
	
	public void changePosition(float curX, float curY, float toX, float toY) {
		
	}
}

package com.sdp.commands;

import com.sdp.RobotCommunication;

public class RobotCommands {

	public void changeDirection(float curDir, float toDir) {
		while (curDir < toDir) {
			RobotCommunication.getInstance().holdLeft();
			curDir++;

		}
		try {
			Thread.sleep(2000);
			RobotCommunication.getInstance().stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void changePosition(float curX, float curY, float toX, float toY) {

	}
}

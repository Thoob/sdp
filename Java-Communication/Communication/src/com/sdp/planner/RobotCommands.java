package com.sdp.planner;

import com.sdp.RobotCommunication;
import com.sdp.world.DynamicWorldState.Robot;
import com.sdp.world.Point2;

/**
 *	Higher-level robot commands
 */
public class RobotCommands {
	public void changeHeading(Robot robot, double desiredDir) {

	}

	public void changePosition(Robot robot, Point2 desiredPos) {

	}
	
	public static void rotateRight(){
		RobotCommunication.getInstance().move(0, 40);
	}

	public static void goStraight() {
		RobotCommunication.getInstance().move(40, 30); 
		// TODO fix by replacing motors!		
	}
}

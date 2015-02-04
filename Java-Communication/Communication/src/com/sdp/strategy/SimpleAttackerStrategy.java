package com.sdp.strategy;

import com.sdp.Communication;
import com.sdp.RobotCommunication;
import com.sdp.planner.RobotPlanner;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;

public class SimpleAttackerStrategy extends GeneralStrategy {

	public void sendWorldState(DynamicWorldState dynWorldState) {
		Robot robot = dynWorldState.getAttacker();
		Ball ball = dynWorldState.getBall();

		if (!Communication.getInstance().isPortInitialized()) {
			System.out.println("Port not initialized");
			return;
		}

		// 1. change direction so that robot looks towards the ball
		double diffInHeadings = RobotPlanner.getInstance()
				.differenceInHeadings(robot, ball);
		boolean facingBall;
		if (diffInHeadings < 0.4) {
			facingBall = true;
		} else {
			facingBall = false;
		}
		// Decide which direction to rotate
		if (diffInHeadings > 3.14 && !facingBall) {
			// rotate right
			RobotCommunication.getInstance().move(
					(int) ((diffInHeadings - 3.14) * -30),
					(int) ((diffInHeadings - 3.14) * 30));
		} else if (diffInHeadings < 3.14 && !facingBall) {
			// rotate left
			RobotCommunication.getInstance().move((int) (diffInHeadings * 30),
					(int) (diffInHeadings * -30));
		}

		// 2. go straight until you can catch the ball
		boolean canCatchBall = RobotPlanner.getInstance().canCatchBall(robot,
				ball);
		boolean doesOurRobotHaveBall = RobotPlanner.getInstance()
				.doesOurRobotHaveBall(robot, ball);
		if (!canCatchBall && !doesOurRobotHaveBall && facingBall) {
			RobotCommunication.getInstance().move(60, 60); // TODO fix this
															// command, buffer
															// inputs on
															// arduino?
			return;
		}

		// 3. catch the ball if we don't have it
		// if(canCatchBall && facingBall){
		// RobotCommunication.getInstance().sendCatch();
		// }

		// 4. go to a position from which robot can score

		// 5. score
		// if(canScore){
		// RobotCommunication.getInstance().sendKick(300);
		// }
	}
}

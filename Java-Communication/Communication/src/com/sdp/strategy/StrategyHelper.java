package com.sdp.strategy;

import com.sdp.planner.RobotCommands;
import com.sdp.planner.RobotPlanner;
import com.sdp.world.SimpleWorldState;
import com.sdp.world.SimpleWorldState.Operation;
import com.sdp.world.WorldState;

/**
 * 
 * This class contains high-level strategy methods which could be used in
 * several strategies
 * 
 */
public class StrategyHelper extends GeneralStrategy {

	int facingCounter = 0;

	void acquireBall(WorldState worldState) {
		// System.out.println("trying to acquire the ball");
		initializeVars(worldState);
		// Desired angle to face ball
		double ballAngleDeg = RobotPlanner.desiredAngle(robotX, robotY, ballX,
				ballY);
		double ballDiffInHeadings = Math.abs(robotAngleDeg - ballAngleDeg);
		// Robot is facing the ball if within this angle in degrees of the ball
		boolean isRobotFacingBall = (ballDiffInHeadings < allowedDegreeError || ballDiffInHeadings > 360 - allowedDegreeError);
		
		// 1 - Rotate to face ball
		System.out.println("Current robot heading:" + robotAngleDeg);
		System.out.println("Angle to face:" + ballAngleDeg);
		if (!RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)
				&& !isRobotFacingBall) {
			rotateToDesiredAngle(robotAngleDeg, ballAngleDeg);
			System.out.println("Rotating to face ball.");
			facingCounter = 0;
		} else {
			System.out.println("Desired angle");
			facingCounter++;
			System.out.println("Ball in zone " + RobotPlanner.inZone(ballX));
			System.out.println("Robot in zone " + RobotPlanner.inZone(robotX));
		}

		// 2 - Go towards ball if it is in our zone
		if (facingCounter > 20
				&& !RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX,
						ballY)
				&& !RobotPlanner.canCatchBall(robotX, robotY, ballX, ballY)
				&& (RobotPlanner.inZone(ballX) == RobotPlanner.inZone(robotX))) {

			RobotCommands.goStraight();
			SimpleWorldState.previousOperation = Operation.NONE;
			System.out.println("Moving towards ball.");
		}
		
		// 3 - Catch ball
		if (!RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)
				&& isRobotFacingBall
				&& RobotPlanner.canCatchBall(robotX, robotY, ballX, ballY)
				&& !(SimpleWorldState.previousOperation == Operation.CATCH)) {
			RobotCommands.catchBall();
			SimpleWorldState.previousOperation = Operation.CATCH;
			System.out.println("Catching ball.");
		}

	}

	void rotateToDesiredAngle(double robotAngleDeg, double desiredAngleDeg) {
		double diffInHeadings = Math.abs(robotAngleDeg - desiredAngleDeg);
		System.out.println("Difference in headings: " + diffInHeadings);
		if ((diffInHeadings < allowedDegreeError)
				|| (diffInHeadings > 360 - allowedDegreeError)) {
			// stopping rotation but not other operations
			RobotCommands.stop();
			SimpleWorldState.previousOperation = Operation.NONE;

		} else {
			boolean shouldRotateRight = RobotPlanner.shouldRotateRight(
					desiredAngleDeg, robotAngleDeg);
			if (shouldRotateRight) {
				RobotCommands.shortRotateRight();
				SimpleWorldState.previousOperation = Operation.SHORT_RIGHT;
			} else if (!shouldRotateRight) {
				RobotCommands.shortRotateLeft();
				SimpleWorldState.previousOperation = Operation.SHORT_LEFT;
			}
		}
	}

}
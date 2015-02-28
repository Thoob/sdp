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

	void acquireBall(WorldState worldState) {
		//System.out.println("trying to acquire the ball");
		initializeVars(worldState);
		double robotAngleRad = Math.toRadians(robotAngleDeg);
		// Desired angle to face ball
		double ballAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				robotAngleRad, ballX, ballY);
		double ballDiffInHeadings = Math.abs(robotAngleDeg - ballAngleDeg);
		// Robot is facing the ball if within this angle in degrees of the ball
		boolean isRobotFacingBall = (ballDiffInHeadings < allowedDegreeError || ballDiffInHeadings > 360 - allowedDegreeError);		// 1 - Rotate to face ball
		if (!RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)
				&& !isRobotFacingBall) {
			rotateToDesiredAngle(robotAngleDeg, ballAngleDeg);
			System.out.println("Rotating to face ball.");
		}
		/*
		// 2 - Go towards ball if it is in our attacker zone
		if (worldState.weAreShootingRight) {
			if (!RobotPlanner
					.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)
					&& isRobotFacingBall
					&& !RobotPlanner.canCatchBall(robotX, robotY, ballX, ballY)) {
				RobotCommands.goStraight();
				SimpleWorldState.previousOperation = Operation.NONE;
				System.out.println("Moving towards ball.");
			}
		} else {
			if (!RobotPlanner
					.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)
					&& isRobotFacingBall
					&& !RobotPlanner.canCatchBall(robotX, robotY, ballX, ballY)) {
				RobotCommands.goStraight();
				SimpleWorldState.previousOperation = Operation.NONE;
				System.out.println("Moving towards ball.");
			}
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
		*/
	}

	 void rotateToDesiredAngle(double robotAngleDeg,
			double desiredAngleDeg) {
		double diffInHeadings = Math.abs(robotAngleDeg - desiredAngleDeg);
		System.out.println("Difference in headings: " + diffInHeadings);
		if ((diffInHeadings < allowedDegreeError)
				|| (diffInHeadings > 360 - allowedDegreeError)) {

			System.out.println("Desired angle");
			// stopping rotation but not other operations
			RobotCommands.stop();
			SimpleWorldState.previousOperation = Operation.NONE;
			//if (SimpleWorldState.previousOperation == Operation.RIGHT
			//		|| SimpleWorldState.previousOperation == Operation.LEFT) {
			//	RobotCommands.stop();
			//	SimpleWorldState.previousOperation = Operation.NONE;
			//}
		} else {
			System.out.println("Current robot heading:" + robotAngleDeg);
			System.out.println("Angle to face:" + desiredAngleDeg);

			if ((diffInHeadings < allowedDegreeError * 2)
					|| (diffInHeadings > 360 - allowedDegreeError * 2)) {
				RobotCommands.stop();
				boolean shouldRotateRight = RobotPlanner.shouldRotateRight(
						desiredAngleDeg, robotAngleDeg);
				if (shouldRotateRight) {
					RobotCommands.shortRotateRight();
					SimpleWorldState.previousOperation = Operation.SHORT_RIGHT;
				} else if (!shouldRotateRight) {
					RobotCommands.shortRotateLeft();
					SimpleWorldState.previousOperation = Operation.SHORT_LEFT;
				}
				return;
			} else {
				boolean shouldRotateRight = RobotPlanner.shouldRotateRight(
						desiredAngleDeg, robotAngleDeg);
				if (shouldRotateRight
						&& SimpleWorldState.previousOperation != Operation.RIGHT) {
					RobotCommands.rotateRight();
					SimpleWorldState.previousOperation = Operation.NONE;
				} else if (!shouldRotateRight
						&& SimpleWorldState.previousOperation != Operation.LEFT) {
					RobotCommands.rotateLeft();
					SimpleWorldState.previousOperation = Operation.NONE;
				}
				return;
			}
		}
	}

}

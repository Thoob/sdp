package com.sdp.strategy;

import com.sdp.planner.RobotCommands;
import com.sdp.planner.RobotPlanner;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.SimpleWorldState;
import com.sdp.world.SimpleWorldState.Operation;
import com.sdp.world.WorldState;

public class AttackerStrategy extends GeneralStrategy {

	public void sendWorldState(DynamicWorldState dynWorldState,
			WorldState worldState) {
		if (robot == null || ball == null)
			return;
		// Desired angle to face ball
		double ballAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				robotAngleRad, ballX, ballY);
		double ballDiffInHeadings = Math.abs(robotAngleDeg - ballAngleDeg);
		// Robot is facing the ball if within this angle in degrees of the ball
		boolean isRobotFacingGoal = (ballDiffInHeadings < allowedDegreeError || ballDiffInHeadings > 360 - allowedDegreeError);

		// 1 - Rotate to face ball
		if (!RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)
				&& !isRobotFacingGoal) {
			rotateToDesiredAngle(robotAngleDeg, ballAngleDeg);
			System.out.println("Rotating to face ball.");
		}

		// 2 - Go towards ball if it is in our attacker zone
		System.out.println("Ball is in zone " + inZone(ballX));
		if (worldState.weAreShootingRight) {
			if (!RobotPlanner
					.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)
					&& isRobotFacingGoal
					&& !RobotPlanner.canCatchBall(robotX, robotY, ballX, ballY)
					&& (inZone(ballX) == 2)) {
				RobotCommands.goStraight();
				SimpleWorldState.previousOperation = Operation.NONE;
				System.out.println("Moving towards ball.");
			}
		} else {
			if (!RobotPlanner
					.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)
					&& isRobotFacingGoal
					&& !RobotPlanner.canCatchBall(robotX, robotY, ballX, ballY)
					&& (inZone(ballX) == 1)) {
				RobotCommands.goStraight();
				SimpleWorldState.previousOperation = Operation.NONE;
				System.out.println("Moving towards ball.");
			}
		}

		// 3 - Catch ball
		if (!RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)
				&& isRobotFacingGoal
				&& RobotPlanner.canCatchBall(robotX, robotY, ballX, ballY)
				&& !(SimpleWorldState.previousOperation == Operation.CATCH)) {
			RobotCommands.catchBall();
			SimpleWorldState.previousOperation = Operation.CATCH;
			System.out.println("Catching ball.");
		}

		// 4 - Face goal and kick ball (hopefully into the goal!)
		if (RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)) {
			scoreGoal(dynWorldState, worldState);
			System.out.println("Scoring goal!");
		}
	}

	private void rotateToDesiredAngle(double robotAngleDeg,
			double desiredAngleDeg) {
		double diffInHeadings = Math.abs(robotAngleDeg - desiredAngleDeg);
		System.out.println("Difference in headings: " + diffInHeadings);
		if ((diffInHeadings < allowedDegreeError)
				|| (diffInHeadings > 360 - allowedDegreeError)) {

			System.out.println("Desired angle");
			// stopping rotation but not other operations
			if (SimpleWorldState.previousOperation == Operation.RIGHT
					|| SimpleWorldState.previousOperation == Operation.LEFT) {
				RobotCommands.stop();
				SimpleWorldState.previousOperation = Operation.NONE;
			}
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

	private void scoreGoal(DynamicWorldState dynWorldState,
			WorldState worldState) {
		boolean facingGoal = false;

		System.out.println("goal " + leftGoalX + " " + leftGoalY);
		System.out.println("robot " + robotX + " " + robotY);
		System.out.println("ball " + ballX + " " + ballY);

		double desiredAngleDegb = RobotPlanner.desiredAngle(robotX, robotY,
				robotAngleRad, ballX, ballY);

		System.out.println("desiredAngleBall " + desiredAngleDegb);

		// Decide which goal to aim at, and calculate desired angle
		double desiredAngleDeg = 0.0;
		if (worldState.weAreShootingRight) {
			desiredAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
					robotAngleRad, rightGoalX, rightGoalY);
		} else {
			desiredAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
					robotAngleRad, leftGoalX, leftGoalY);
		}

		System.out.println("desiredAngleGoal " + desiredAngleDeg);
		rotateToDesiredAngle(robotAngleDeg, desiredAngleDeg);

		// Decides whether or not the robot is facing the desired goal
		if (Math.abs(robotAngleDeg - desiredAngleDeg) < allowedDegreeError) {
			facingGoal = true;
			System.out.println("Facing goal!");
		} else {
			facingGoal = false;
			System.out.println("Not facing goal.");
		}

		if (SimpleWorldState.previousOperation != Operation.KICK
				&& facingGoal
				&& RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX,
						ballY)) { // change
			// to
			// doesRobotHaveBall
			RobotCommands.kick();
			SimpleWorldState.previousOperation = Operation.KICK;
		}
	}
}

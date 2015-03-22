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

	private boolean isRobotFacingBall = false;
	private boolean isRobotFacingTarget = false;
	private boolean isNearTarget = false;

	void acquireBall(WorldState worldState) {
		System.out.println("trying to acquire the ball");
		initializeVars(worldState);
		// Desired angle to face ball
		double ballAngleDeg = RobotPlanner.desiredAngle(robotX, robotY, ballX,
				ballY);
		double ballDiffInHeadings = Math.abs(robotAngleDeg - ballAngleDeg);
		// Robot is facing the ball if within this angle in degrees of the ball
		isRobotFacingBall = (ballDiffInHeadings < allowedDegreeError || ballDiffInHeadings > 360 - allowedDegreeError);

		// 1 - Rotate to face ball
		if (!RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)
				&& !isRobotFacingBall) {
			rotateToDesiredAngle(robotAngleDeg, ballAngleDeg);
			System.out.println("Rotating to face ball.");
		}

		// 2 - Go towards ball if it is in our zone
		/* Frame counter may be useful here later */
		if (isRobotFacingBall
				&& !RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX,
						ballY)
				&& !RobotPlanner.canCatchBall(robotX, robotY, ballX, ballY)
				&& (RobotPlanner.inZone(ballX, worldState) == RobotPlanner
						.inZone(robotX, worldState))) {

			RobotCommands.goStraight();
			SimpleWorldState.previousOperation = Operation.NONE;
			System.out.println("Moving towards ball.");
		}

		// 3 - Prepare to catch ball
		if (!RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)
				&& RobotPlanner.prepareCatch(robotX, robotY, ballX, ballY)
				&& !(SimpleWorldState.previousOperation == Operation.CATCH)) {
			RobotCommands.catchUp();
			System.out.println("Preparing to catch ball.");
		}

		// 4 - Catch ball
		if (!RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)
				&& isRobotFacingBall
				&& RobotPlanner.canCatchBall(robotX, robotY, ballX, ballY)
				&& !(SimpleWorldState.previousOperation == Operation.CATCH)) {
			RobotCommands.catchDown();
			SimpleWorldState.previousOperation = Operation.CATCH;
			System.out.println("Catching ball.");
		}
	}

	void rotateToDesiredAngle(double robotAngleDeg, double desiredAngleDeg) {
		double diffInHeadings = Math.abs(robotAngleDeg - desiredAngleDeg);
		if ((diffInHeadings < allowedDegreeError)
				|| (diffInHeadings > 360 - allowedDegreeError)) {
			// stopping rotation but not other operations
			if (SimpleWorldState.previousOperation == Operation.RIGHT
					|| SimpleWorldState.previousOperation == Operation.LEFT) {
				RobotCommands.stop();
				SimpleWorldState.previousOperation = Operation.NONE;
			}
		} else {
			if ((diffInHeadings < allowedDegreeError * 2)
					|| (diffInHeadings > 360 - allowedDegreeError * 2)) {
				if (SimpleWorldState.previousOperation == Operation.RIGHT
						|| SimpleWorldState.previousOperation == Operation.LEFT) {
					RobotCommands.stop();
				}
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
					SimpleWorldState.previousOperation = Operation.RIGHT;
				} else if (!shouldRotateRight
						&& SimpleWorldState.previousOperation != Operation.LEFT) {
					RobotCommands.rotateLeft();
					SimpleWorldState.previousOperation = Operation.LEFT;
				}
				return;
			}
		}
	}

	void rotateToDesiredAngleForDef(double robotAngleDeg, double desiredAngleDeg) {
		double diffInHeadings = Math.abs(robotAngleDeg - desiredAngleDeg);
		if ((diffInHeadings < 25) || (diffInHeadings > 360 - 25)) {
			if (SimpleWorldState.previousOperation == Operation.RIGHT
					|| SimpleWorldState.previousOperation == Operation.LEFT) {
				RobotCommands.brakeStop();
				SimpleWorldState.previousOperation = Operation.NONE;
			}
		} else {
			boolean shouldRotateRight = RobotPlanner.shouldRotateRight(
					desiredAngleDeg, robotAngleDeg);
			if (shouldRotateRight
					&& SimpleWorldState.previousOperation != Operation.RIGHT) {
				RobotCommands.rotateRight();
				SimpleWorldState.previousOperation = Operation.RIGHT;
			} else if (!shouldRotateRight
					&& SimpleWorldState.previousOperation != Operation.LEFT) {
				RobotCommands.rotateLeft();
				SimpleWorldState.previousOperation = Operation.LEFT;
			}
			return;
		}
	}

	void goToForDef(double targetX, double targetY, double robotX,
			double robotY, WorldState worldState) {
		initializeVars(worldState);

		isNearTarget = (RobotPlanner.nearTargetForDef(robotY, targetY));
		System.out.println("goTo for def " + targetY + " robotY " + robotY
				+ " near " + isNearTarget);
		if (isNearTarget || !isSameZone(robotX, targetX, worldState)) {
			RobotCommands.brakeStop();
			//RobotCommands.stop();
		} else {
			// Desired angle to face target
			double targetAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
					targetX, targetY);

			boolean shouldMoveBackwards = RobotPlanner.shouldMoveBackwards(
					robotAngleDeg, targetAngleDeg);
			if (shouldMoveBackwards)
				targetAngleDeg = RobotPlanner.getOppositeAngle(targetAngleDeg);

			double targetDiffInHeadings = Math.abs(robotAngleDeg
					- targetAngleDeg);
			System.out.println("targetAngleDeg = " + targetAngleDeg
					+ " robotAngle " + robotAngleDeg + " diffInHeadings= "
					+ targetDiffInHeadings);

			boolean isRobotFacingTarget = (targetDiffInHeadings < allowedDegreeError || targetDiffInHeadings > 360 - allowedDegreeError);
			// 1 - Rotate to face target
			if (!isRobotFacingTarget && !isNearTarget) {
				rotateToDesiredAngleForDef(robotAngleDeg, targetAngleDeg);
				return;
			}

			if (!shouldMoveBackwards) {
				RobotCommands.goStraight(robotX, robotY, targetX, targetY);
				SimpleWorldState.previousOperation = Operation.FORWARD;
			} else if (shouldMoveBackwards) {
				RobotCommands.goStraightBackwards(robotX, robotY, targetX,
						targetY);
				SimpleWorldState.previousOperation = Operation.BACKWARD;
			}
		}
	}

	void goTo(double targetX, double targetY, double robotX, double robotY,
			WorldState worldState) {
		initializeVars(worldState);

		isNearTarget = (RobotPlanner.nearTarget(robotX, robotY, targetX,
				targetY));
		// Desired angle to face target
		double targetAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				targetX, targetY);

		boolean shouldMoveBackwards = RobotPlanner.shouldMoveBackwards(
				robotAngleDeg, targetAngleDeg);
		if (shouldMoveBackwards)
			targetAngleDeg = RobotPlanner.getOppositeAngle(targetAngleDeg);

		double targetDiffInHeadings = Math.abs(robotAngleDeg - targetAngleDeg);
		// Robot is facing the target if within this angle in allowedDegreeError
		// of the target
		isRobotFacingTarget = (targetDiffInHeadings < allowedDegreeError || targetDiffInHeadings > 360 - allowedDegreeError);
		// 1 - Rotate to face target
		if (!isRobotFacingTarget && !isNearTarget) {
			rotateToDesiredAngle(robotAngleDeg, targetAngleDeg);
			return;
		}

		// 2 - Go towards target if it is in our zone
		// Go forwards or backwards depending on which side of the pitch we are
		if (shouldMoveForward(targetX, targetY, worldState)
				&& !shouldMoveBackwards) {
			RobotCommands.goStraight(robotX, robotY, targetX, targetY);
			SimpleWorldState.previousOperation = Operation.FORWARD;
		} else if (shouldMoveBackward(targetX, targetY, worldState)
				&& shouldMoveBackwards) {
			RobotCommands.goStraightBackwards(robotX, robotY, targetX, targetY);
			SimpleWorldState.previousOperation = Operation.BACKWARD;
		} else if (RobotPlanner.nearTarget(robotX, robotY, targetX, targetY)) {
			if (SimpleWorldState.previousOperation == Operation.FORWARD
					|| SimpleWorldState.previousOperation == Operation.BACKWARD)
				RobotCommands.stop();

			// 3 - Stop once we've reached target and rotate to neutral defender
			double neutralAngle = (robotAngleDeg > 180) ? 270 : 90;
			rotateToDesiredAngle(robotAngleDeg, neutralAngle);
		}
	}
	
	void goToPoint(double targetX, double targetY, double robotX, double robotY,
					WorldState worldState) {
		System.out.println("trying to acquire the ball (GTP)");
		initializeVars(worldState);
		// Desired angle to face ball
		double ballAngleDeg = RobotPlanner.desiredAngle(robotX, robotY, targetX,
				targetY);
		double ballDiffInHeadings = Math.abs(robotAngleDeg - ballAngleDeg);
		// Robot is facing the ball if within this angle in degrees of the ball
		boolean isRobotFacingtarget = (ballDiffInHeadings < allowedDegreeError || ballDiffInHeadings > 360 - allowedDegreeError);

		// 1 - Rotate to face ball
		if (!isRobotFacingtarget) {
			rotateToDesiredAngle(robotAngleDeg, ballAngleDeg);
			System.out.println("Rotating");
		}

		// 2 - Go towards ball if it is in our zone
		/* Frame counter may be useful here later */
		if (isRobotFacingBall
				&& (RobotPlanner.inZone(ballX, worldState) == RobotPlanner
						.inZone(robotX, worldState))) {
			RobotCommands.goStraight();
			SimpleWorldState.previousOperation = Operation.NONE;
			System.out.println("Moving.");
		}
	}

	private boolean shouldMoveBackward(double targetX, double targetY,
			WorldState worldState) {
		return isRobotFacingTarget && isSameZone(robotX, targetX, worldState)
				&& !isNearTarget;
	}

	private boolean shouldMoveForward(double targetX, double targetY,
			WorldState worldState) {
		return isRobotFacingTarget && isSameZone(robotX, targetX, worldState)
				&& !isNearTarget;
	}

	private boolean isSameZone(double robotX, double targetX,
			WorldState worldState) {
		return (RobotPlanner.inZone(targetX, worldState) == RobotPlanner
				.inZone(robotX, worldState));
	}
}

package com.sdp.strategy;

import com.sdp.planner.RobotCommands;
import com.sdp.planner.RobotPlanner;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;
import com.sdp.world.SimpleWorldState;
import com.sdp.world.SimpleWorldState.Operation;
import com.sdp.world.WorldState;

public class SimpleAttackerStrategy extends GeneralStrategy {
	private final int allowedDegreeError = 30;
	private boolean isRobotFacingBall = false;
	private boolean isRobotFacingGoal = false;

	public void sendWorldState(DynamicWorldState dynWorldState,
			WorldState worldState) {
		// Initialise robot and ball objects
		Robot robot = dynWorldState.getAttacker();
		Ball ball = dynWorldState.getBall();

		// Values that we need to calculate
		// Robot position (x,y) and heading in degrees
		double robotX = robot.getCenter().getX();
		double robotY = robot.getCenter().getY();
		double robotDir = robot.getHeading();
		double robotAngleDeg = Math.toDegrees(robotDir);
		// Ball position (x,y)
		double ballX = ball.getPoint().getX();
		double ballY = ball.getPoint().getY();
		// Desired angle to face ball
		double ballAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				robotDir, ballX, ballY);
		double ballDiffInHeadings = Math.abs(robotAngleDeg - ballAngleDeg);
		// Robot is facing the ball if within this angle in degrees of the ball
		isRobotFacingBall = (ballDiffInHeadings < allowedDegreeError || ballDiffInHeadings > 360 - allowedDegreeError);
		// Desired angle to face goal (goalX and centre of goalY)
		// double goalAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
		// robotDir, goalX, goalY[1]);

		 // 1 - Rotate to face ball
		 if(!RobotPlanner.doesOurRobotHaveBall(robot, ball) &&
		 !isRobotFacingBall){
		 rotateToDesiredAngle(robotAngleDeg, ballAngleDeg);
		 System.out.println("Rotating to face ball.");
		 }
		
		 // 2 - Go towards ball
		 if(!RobotPlanner.doesOurRobotHaveBall(robot, ball) &&
		 isRobotFacingBall && !RobotPlanner.canCatchBall(robot, ball)){
		 //TODO needs to only happen when the ball is in our zone
		 RobotCommands.goStraight();
		 SimpleWorldState.previousOperation = Operation.FORWARD;
		 System.out.println("Moving towards ball.");
		 }
		
		 // 3 - Catch ball
		 if(!RobotPlanner.doesOurRobotHaveBall(robot, ball)
		 && isRobotFacingBall
		 && RobotPlanner.canCatchBall(robot, ball)
		 && !(SimpleWorldState.previousOperation == Operation.CATCH)){
		 RobotCommands.catchBall();
		 SimpleWorldState.previousOperation = Operation.CATCH;
		 System.out.println("Catching ball.");
		 }
		
		 // 4 - Face goal and kick ball (hopefully into the goal!)
		 if(RobotPlanner.doesOurRobotHaveBall(robot, ball)){
		 scoreAGoal(dynWorldState, worldState);
		 System.out.println("Scoring goal!");
		 }
	}

	private void rotateToDesiredAngle(double robotAngleDeg,
			double desiredAngleDeg) {
		double diffInHeadings = Math.abs(robotAngleDeg - desiredAngleDeg);
		System.out.println("Difference in headings: " + diffInHeadings);
		if ((diffInHeadings < allowedDegreeError)
				|| (diffInHeadings > 360 - allowedDegreeError)) {
			isRobotFacingBall = true;

			System.out.println("DESIRED ANGLE");
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
				isRobotFacingBall = false;

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
				isRobotFacingBall = false;

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

	private void scoreAGoal(DynamicWorldState dynWorldState,
			WorldState worldState) {
		// turn towards the goal
		Ball ball = dynWorldState.getBall();
		double ballX = ball.getPoint().getX();
		double ballY = ball.getPoint().getY();
		Robot robot = dynWorldState.getAttacker();
		double robotX = robot.getCenter().getX();
		double robotY = robot.getCenter().getY();
		double robotDir = robot.getHeading();
		double robotAngleDeg = Math.toDegrees(robotDir);
		double goalX = SimpleGeneralStrategy.rightGoalX;
		double goalY = SimpleGeneralStrategy.rightGoalY;
		boolean facingGoal = false;
		boolean robotHasBall = RobotPlanner.doesOurRobotHaveBall(robot, ball);

		System.out.println("goal " + goalX + " " + goalY);
		System.out.println("robot " + robotX + " " + robotY);
		System.out.println("ball " + ballX + " " + ballY);

		double desiredAngleDegb = RobotPlanner.desiredAngle(robotX, robotY,
				robotDir, ballX, ballY);

		System.out.println("desiredAngleBall " + desiredAngleDegb);

		double desiredAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				robotDir, goalX, goalY);

		System.out.println("desiredAngleGoal " + desiredAngleDeg);

		rotateToDesiredAngle(robotAngleDeg, desiredAngleDeg);
		
		if(robotAngleDeg - desiredAngleDeg < allowedDegreeError){
			facingGoal = true;
		} else {
			facingGoal = false;
		}
		
		if (SimpleWorldState.previousOperation != Operation.KICK
				&& robotHasBall && facingGoal) { // change to doesRobotHaveBall
			RobotCommands.kick();
			SimpleWorldState.previousOperation = Operation.KICK;
		}
	}
}

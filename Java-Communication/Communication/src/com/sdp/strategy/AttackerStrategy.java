package com.sdp.strategy;

import com.sdp.planner.RobotCommands;
import com.sdp.planner.RobotPlanner;
import com.sdp.world.SimpleWorldState;
import com.sdp.world.SimpleWorldState.Operation;
import com.sdp.world.WorldState;

public class AttackerStrategy extends GeneralStrategy {
	StrategyHelper sh;
	double robotAngleRad;

	public AttackerStrategy() {
		sh = new StrategyHelper();
	}

	public void sendWorldState(WorldState worldState) {
		robotAngleRad = Math.toRadians(robotAngleDeg);
		if (robot == null || ball == null)
			return;
		sh.acquireBall(worldState);

		// 4 - Face goal and kick ball (hopefully into the goal!)
		if (RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)) {
			scoreGoal(worldState);
			System.out.println("Scoring goal!");
		}
	}

	private void scoreGoal(WorldState worldState) {
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
					robotAngleRad, rightGoalX, rightGoalY[1]);
		} else {
			desiredAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
					robotAngleRad, leftGoalX, leftGoalY[1]);
		}

		System.out.println("desiredAngleGoal " + desiredAngleDeg);
		sh.rotateToDesiredAngle(robotAngleDeg, desiredAngleDeg);

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
						ballY)) {
			RobotCommands.kick();
			SimpleWorldState.previousOperation = Operation.KICK;
		}
	}
}

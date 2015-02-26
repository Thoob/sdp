package com.sdp.strategy;

import com.sdp.vision.PitchConstants;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.MovingObject;
import com.sdp.world.WorldState;

public class GeneralStrategy {

	// center coordinates
	protected float leftGoalX = -572;
	protected float[] leftGoalY = new float[3];
	protected float rightGoalX;
	protected float[] rightGoalY = new float[3];

	protected int topOfPitch;
	protected int botOfPitch;

	MovingObject robot;
	MovingObject ball;
	double robotX;
	double robotY;
	double robotAngleRad;
	double robotAngleDeg;
	double ballX;
	double ballY;

	// Allowed errors
	final int allowedDegreeError = 15;
	final int allowedDistError = 20;

	public void sendWorldState(WorldState worldState,
			DynamicWorldState dynWorldState) {
		robot = worldState.getDefenderRobot();
		ball = worldState.getBall();

		robotX = robot.x;
		robotY = robot.y;
		robotAngleRad = robot.orientation_angle;
		robotAngleDeg = Math.toDegrees(robotAngleRad);
		ballX = ball.x;
		ballY = ball.y;

		topOfPitch = PitchConstants.getPitchOutlineTop();
		botOfPitch = PitchConstants.getPitchOutlineBottom();

		leftGoalX = PitchConstants.getPitchOutline()[7].getX();
		leftGoalY = worldState.leftGoal;

		rightGoalX = PitchConstants.getPitchOutline()[3].getX();
		rightGoalY = worldState.rightGoal;
	}

	// Returns the zone an object with a given X value is in
	// I'm so sorry for all these magic numbers! - Theo
	public int inZone(double objX) {
		System.out.println("Ball X: " + (objX));
		if (objX < -324) {
			return 0;
		} else if (objX < 25) {
			return 1;
		} else if (objX < 374) {
			return 2;
		} else if (objX < 650) {
			return 3;
		} else {
			return -1;
		}
	}

	public static double calculateAngle(float robotX, float robotY,
			float robotOrientation, float targetX, float targetY) {
		double robotRad = Math.toRadians(robotOrientation);
		double targetRad = Math.atan2(targetY - robotY, targetX - robotX);

		if (robotRad > Math.PI)
			robotRad -= 2 * Math.PI;

		double ang1 = robotRad - targetRad;
		while (ang1 > Math.PI)
			ang1 -= 2 * Math.PI;
		while (ang1 < -Math.PI)
			ang1 += 2 * Math.PI;
		return Math.toDegrees(ang1);
	}
}

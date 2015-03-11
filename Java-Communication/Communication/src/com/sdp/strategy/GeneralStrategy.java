package com.sdp.strategy;

import com.sdp.vision.PitchConstants;
import com.sdp.world.MovingObject;
import com.sdp.world.WorldState;

public class GeneralStrategy {

	protected static float leftGoalX;
	protected static float rightGoalX;
	protected float[] leftGoalY = new float[3];
	protected float[] rightGoalY = new float[3];

	protected int topOfPitch;
	protected int botOfPitch;
	protected int[] TLCorner;
	protected int[] BLCorner;
	protected int[] TRCorner;
	protected int[] BRCorner;

	protected MovingObject robot;
	protected MovingObject ball;
	protected double robotX;
	protected double robotY;
	protected double robotAngleDeg;

	protected double attackerX;
	protected double attackerY;

	protected double enemyDefenderX;
	protected double enemyDefenderY;
	protected double enemyAttackerX;
	protected double enemyAttackerY;

	protected double ballX;
	protected double ballY;

	// Allowed errors
	protected final int allowedDegreeError = 15;
	protected final int allowedDistError = 20;
	protected WorldState worldState;
	private final static int defaultDistFromGoal = 50;

	public void sendWorldState(WorldState worldState) {
		this.worldState = worldState;
		initializeVars(worldState);
	}

	public static double getOurGoalX(WorldState worldState) {
		double ourGoalX = worldState.weAreShootingRight ? leftGoalX
				+ defaultDistFromGoal : rightGoalX - defaultDistFromGoal;
		return ourGoalX;
	}

	public static double getOurGoalY(WorldState worldState) {
		return (worldState.weAreShootingRight) ? worldState.leftGoal[1]
				: worldState.rightGoal[1];
	}

	public static double calculateAngle(double robotX, double robotY,
			double robotOrientation, double targetX, double targetY) {
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

	void initializeVars(WorldState worldState) {
		robot = (MovingObject) worldState.getDefenderRobot();
		ball = (MovingObject) worldState.getBall();
		robotX = robot.x;
		robotY = robot.y;
		robotAngleDeg = robot.orientationAngle;
		ballX = ball.x;
		ballY = ball.y;

		MovingObject attacker = worldState.getAttackerRobot();
		attackerX = attacker.x;
		attackerY = attacker.y;

		MovingObject enemyDefender = worldState.getEnemyDefenderRobot();
		enemyDefenderX = enemyDefender.x;
		enemyDefenderY = enemyDefender.y;

		MovingObject enemyAttacker = worldState.getEnemyAttackerRobot();
		enemyAttackerX = enemyAttacker.x;
		enemyAttackerY = enemyAttacker.y;

		topOfPitch = PitchConstants.getPitchOutlineTop();
		botOfPitch = PitchConstants.getPitchOutlineBottom();

		leftGoalX = PitchConstants.getPitchOutline()[7].getX();
		leftGoalY = worldState.leftGoal;

		rightGoalX = PitchConstants.getPitchOutline()[3].getX();
		rightGoalY = worldState.rightGoal;

		TLCorner = PitchConstants.getPitchOutlineTL();
		TRCorner = PitchConstants.getPitchOutlineTR();
		BLCorner = PitchConstants.getPitchOutlineBL();
		BRCorner = PitchConstants.getPitchOutlineBR();

	}

}

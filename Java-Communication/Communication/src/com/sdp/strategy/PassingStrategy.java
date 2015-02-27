package com.sdp.strategy;

import com.sdp.planner.RobotPlanner;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Robot;
import com.sdp.world.WorldState;

public class PassingStrategy extends GeneralStrategy {
	/*
	 * why do we need these???? they are not used anywhere... protected boolean
	 * ballIsOnSlopeEdge; protected boolean ballIsOnSideEdge; protected boolean
	 * ballIsOnGoalLine; protected boolean ballIsOnDefCheck; protected boolean
	 * robotIsOnGoalLine; protected boolean catcherIsUp = false; protected
	 * boolean affectBallCaught = true; protected boolean defenderHasArrived =
	 * false; protected boolean defenderHasArrivedAtSafe = false; protected
	 * boolean defenderIsSafe = false; protected double defenderAngleToGoal;
	 * protected double distFromBall;
	 */
	public PassingStrategy() {
	}

	public void sendWorldState(DynamicWorldState dynWorldState,
			WorldState worldState) {
		if (robot == null || ball == null)
			return;
		// ROBOT DECLARATIONS //
		Robot attackingRobot = dynWorldState.getAttacker();
		Robot enemyDefender = dynWorldState.getEnemyDefender();

		// ROBOT COORDINATES //
		double attackerX = attackingRobot.getCenter().getX();
		double attackerY = attackingRobot.getCenter().getY();
		double enemyDefenderX = enemyDefender.getCenter().getX();
		double enemyDefenderY = enemyDefender.getCenter().getY();

		// FACING ANGLES //
		double attackerAngle = RobotPlanner.desiredAngle(robotX, robotY,
				robotAngleRad, attackerX, attackerY);
		double enemyDefenderAngle = RobotPlanner.desiredAngle(robotX, robotY,
				robotAngleRad, enemyDefenderX, enemyDefenderY);
		double diffInHeadingsToAttacker = Math.abs(robotAngleDeg
				- attackerAngle);
		double diffInHeadingsToBlocker = Math.abs(robotAngleDeg
				- enemyDefenderAngle);

		// STATE BOOLEANS //
		/* We are currently facing the attacker */
		boolean facingAttacker = isFacingAttacker(diffInHeadingsToAttacker);
		/* The Blocker is currently outwith our facing angle */
		boolean enemyBlocking = isEnemyBlocking(diffInHeadingsToBlocker);
		/* We are facing the attacker with no obstruction */
		boolean attackerInLineOfSight = (facingAttacker && enemyBlocking);

		System.out.println("Attacker " + attackerX + " " + attackerY
				+ " Heading diff: " + diffInHeadingsToAttacker);
		System.out.println("Blocker " + enemyDefenderX + " " + enemyDefenderY);

		/* DEBUG */
		if (attackerInLineOfSight)
			System.out.println("Attacker is in line of sight");
		else if (enemyBlocking)
			System.out.println("Enemy is blocking");
		else if (facingAttacker)
			System.out.println("We are facing Attacker");

	}

	private boolean isEnemyBlocking(double diffInHeadingsToBlocker) {
		return !(diffInHeadingsToBlocker > allowedDegreeError || diffInHeadingsToBlocker < 360 - allowedDegreeError);
	}

	private boolean isFacingAttacker(double diffInHeadingsToAttacker) {
		return (diffInHeadingsToAttacker < allowedDegreeError || diffInHeadingsToAttacker > 360 - allowedDegreeError);
	}
}
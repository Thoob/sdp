package com.sdp.strategy;

import com.sdp.planner.RobotPlanner;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Robot;
import com.sdp.world.WorldState;

public class PassingStrategy extends GeneralStrategy {
	protected boolean ballIsOnSlopeEdge;
	protected boolean ballIsOnSideEdge;
	protected boolean ballIsOnGoalLine;
	protected boolean ballIsOnDefCheck;
	protected boolean robotIsOnGoalLine;
	protected boolean catcherIsUp = false;
	protected boolean affectBallCaught = true;
	protected boolean defenderHasArrived = false;
	protected boolean defenderHasArrivedAtSafe = false;
	protected boolean defenderIsSafe = false;
	protected double defenderAngleToGoal;
	protected double distFromBall;

	public PassingStrategy() {

	}

	public void sendWorldState(DynamicWorldState dynWorldState,
			WorldState worldState) {
		// TODO: Test! - somewhat difficulty in recognising 3 robots across the
		// pitch
		// ROBOT DECLARATIONS //
		Robot attackingRobot = dynWorldState.getAttacker();
		Robot enemyDefender = dynWorldState.getEnemyDefender();

		// ROBOT COORDANATES //
		double attackerX = attackingRobot.getCenter().getX();
		double attackerY = attackingRobot.getCenter().getY();
		double enemyDefenderX = enemyDefender.getCenter().getX();
		double enemyDefenderY = enemyDefender.getCenter().getY();

		// FACING ANGLES //
		double robotAngleRad = robot.getHeading();
		double robotAngleDeg = Math.toDegrees(robotAngleRad);

		double attackerAngle = RobotPlanner.desiredAngle(robotX, robotY,
				robotAngleRad, attackerX, attackerY);
		double enemyDefenderAngle = RobotPlanner.desiredAngle(robotX, robotY,
				robotAngleRad, enemyDefenderX, enemyDefenderY);
		double diffInHeadingsToAttacker = Math.abs(robotAngleDeg
				- attackerAngle);
		double diffInHeadingstoBlocker = Math.abs(robotAngleDeg
				- enemyDefenderAngle);

		// STATE BOOLEANS //
		/* We are currently facing the attacker */
		boolean facingAttacker = (diffInHeadingsToAttacker < allowedDegreeError || diffInHeadingsToAttacker > 360 - allowedDegreeError);
		/* The Blocker is currently outwith our facing angle */
		boolean noObstruction = (diffInHeadingstoBlocker > allowedDegreeError || diffInHeadingstoBlocker < 360 - allowedDegreeError);
		/* We are facing the attacker with no obstruction */
		boolean inLineOfSight = (facingAttacker && noObstruction);

		System.out.println("Attacker " + attackerX + " " + attackerY
				+ " Heading diff: " + diffInHeadingsToAttacker);
		System.out.println("Blocker " + enemyDefenderX + " " + enemyDefenderY);

		/* DEBUG */
		if (!noObstruction) {
			System.out.println("Enemy is blocking");
		}
		if (facingAttacker) {
			System.out.println("We are facing Attacker");
		}
		if (inLineOfSight) {
			System.out.println("Attacker is in line of sight");
		}

	}
}
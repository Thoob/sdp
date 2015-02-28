package com.sdp.strategy;

import com.sdp.planner.RobotPlanner;
import com.sdp.world.WorldState;

/**
 * This strategy is used when don't have the ball but it is in our part of the
 * pitch
 */
public class PassingStrategy extends GeneralStrategy {
	StrategyHelper sh;

	public PassingStrategy() {
		sh = new StrategyHelper();
	}

	public void sendWorldState(WorldState worldState) {
		sh.acquireBall(worldState);
		// TODO fix this??
		
		initializeVars(worldState);
		// get position of our robot and attacker!

//		System.out.println("our position " + robotX + " " + robotY+" "+robotAngleDeg);
//		System.out.println("attacker position " + attackerX + " " + attackerY);

		// STATE BOOLEANS //
		boolean facingAttacker = isFacingAttacker();
		boolean enemyBlocking = isEnemyBlocking();
		boolean attackerInLineOfSight = (facingAttacker && enemyBlocking);

		/*
		 * if (attackerInLineOfSight)
		 * System.out.println("Attacker is in line of sight"); else if
		 * (enemyBlocking) System.out.println("Enemy is blocking"); else if
		 * (facingAttacker) System.out.println("We are facing Attacker");
		 */
	}

	// TODO fix this
	private boolean isEnemyBlocking() {
		double robotAngleRad = Math.toRadians(robotAngleDeg);
		double enemyDefenderAngle = RobotPlanner.desiredAngle(robotX, robotY,
				robotAngleRad, enemyDefenderX, enemyDefenderY);
		double diffInHeadingsToBlocker = Math.abs(robotAngleDeg
				- enemyDefenderAngle);
		return !(diffInHeadingsToBlocker > allowedDegreeError || diffInHeadingsToBlocker < 360 - allowedDegreeError);
	}

	private boolean isFacingAttacker() {
		double robotAngleRad = Math.toRadians(robotAngleDeg);
		double attackerAngle = RobotPlanner.desiredAngle(robotX, robotY,
				robotAngleRad, attackerX, attackerY);
		double diffInHeadingsToAttacker = Math.abs(robotAngleDeg
				- attackerAngle);
		return (diffInHeadingsToAttacker < allowedDegreeError || diffInHeadingsToAttacker > 360 - allowedDegreeError);
	}
}
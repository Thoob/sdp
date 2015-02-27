package com.sdp.strategy;

import com.sdp.planner.RobotPlanner;
import com.sdp.world.DynamicWorldState;
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

	public void sendWorldState(DynamicWorldState dynWorldState,
			WorldState worldState) {
		if (robot == null || ball == null)
			return;
		
		sh.acquireBall();

		// STATE BOOLEANS //
		boolean facingAttacker = isFacingAttacker();
		boolean enemyBlocking = isEnemyBlocking();
		boolean attackerInLineOfSight = (facingAttacker && enemyBlocking);

		System.out.println("Blocker " + enemyDefenderX + " " + enemyDefenderY);

		/* DEBUG */
		if (attackerInLineOfSight)
			System.out.println("Attacker is in line of sight");
		else if (enemyBlocking)
			System.out.println("Enemy is blocking");
		else if (facingAttacker)
			System.out.println("We are facing Attacker");
	}
	
	// TODO fix this
	private boolean isEnemyBlocking() {
		double enemyDefenderAngle = RobotPlanner.desiredAngle(robotX, robotY,
				robotAngleRad, enemyDefenderX, enemyDefenderY);
		double diffInHeadingsToBlocker = Math.abs(robotAngleDeg
				- enemyDefenderAngle);
		return !(diffInHeadingsToBlocker > allowedDegreeError || diffInHeadingsToBlocker < 360 - allowedDegreeError);
	}

	private boolean isFacingAttacker() {
		double attackerAngle = RobotPlanner.desiredAngle(robotX, robotY,
				robotAngleRad, attackerX, attackerY);
		double diffInHeadingsToAttacker = Math.abs(robotAngleDeg
				- attackerAngle);
		System.out.println("Attacker " + attackerX + " " + attackerY
				+ " Heading diff: " + diffInHeadingsToAttacker);
		return (diffInHeadingsToAttacker < allowedDegreeError || diffInHeadingsToAttacker > 360 - allowedDegreeError);
	}
}
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
		// TODO check if we have the ball if not get it
		// sh.acquireBall(worldState);
		// TODO fix this??
		initializeVars(worldState);
		System.out.println("our position " + robotX + " " + robotY + " "
				+ robotAngleDeg);
		System.out.println("attacker position " + attackerX + " " + attackerY);

		// STATE BOOLEANS //
		boolean facingAttacker = isFacingAttacker();
		boolean enemyBlocking = isEnemyBlocking();
		boolean attackerInLineOfSight = (facingAttacker && enemyBlocking);

		if (attackerInLineOfSight)
			System.out.println("Attacker is in line of sight");
		else if (enemyBlocking)
			System.out.println("Enemy is blocking");
		else if (facingAttacker)
			System.out.println("We are facing Attacker");

	}

	private boolean isEnemyBlocking() {
		double enemyAttackerAngle = RobotPlanner.desiredAngle(robotX, robotY,
				enemyAttackerX, enemyAttackerY);
		double attackerAngle = getAttackerAngle();
		double diffEnemyAngle = Math.min(Math.abs(360 - enemyAttackerAngle),
				Math.abs(enemyAttackerAngle));
		double diffAttackerAngle = Math.min(Math.abs(360 - attackerAngle),
				Math.abs(attackerAngle));

		return diffAttackerAngle + diffEnemyAngle < 20;
	}

	private boolean isFacingAttacker() {
		double attackerAngle = getAttackerAngle();
		double diffInHeadingsToAttacker = Math.abs(robotAngleDeg
				- attackerAngle);
		return (diffInHeadingsToAttacker < allowedDegreeError || diffInHeadingsToAttacker > 360 - allowedDegreeError);
	}

	private double getAttackerAngle() {
		double attackerAngle = RobotPlanner.desiredAngle(robotX, robotY,
				attackerX, attackerY);
		return attackerAngle;

	}
}
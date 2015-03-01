package com.sdp.strategy;

import com.sdp.planner.RobotCommands;
import com.sdp.planner.RobotPlanner;
import com.sdp.prediction.Calculations;
import com.sdp.world.WorldState;

/**
 * This strategy is used when don't have the ball but it is in our part of the
 * pitch
 */
public class PassingStrategy extends GeneralStrategy {
	StrategyHelper sh;
	int facingCounter = 0;

	public PassingStrategy() {
		sh = new StrategyHelper();
	}

	public void sendWorldState(WorldState worldState) {
		
		// TODO check if we have the ball if not get it
		sh.acquireBall(worldState);
//	
//		// TODO fix this??
//		initializeVars(worldState);
//		System.out.println("our position " + robotX + " " + robotY + " "
//				+ robotAngleDeg);
//		System.out.println("attacker position " + attackerX + " " + attackerY
//				+ " " + getAttackerAngle());
//
//		// STATE BOOLEANS //
//		boolean facingAttacker = isFacingAttacker();
//		boolean enemyBlocking = isEnemyBlocking();
//		
//		double desiredAngleA = Calculations.getBounceAngle(robotX, robotY, Math.toRadians(robotAngleDeg), attackerX, attackerY, enemyAttackerX, enemyAttackerY);
//		System.out.println("desired angle "+desiredAngleA);
//
//		if (facingAttacker) {
//			System.out.println("We are facing Attacker");
//			facingCounter++;
//			
//			if (facingCounter >= 20)
//				RobotCommands.passKick();
//
//		} else {
//			facingCounter = 0;
//			double desiredAngle = Calculations.getBounceAngle(robotX, robotY, Math.toRadians(robotAngleDeg), attackerX, attackerY, enemyAttackerX, enemyAttackerY);
//			System.out.println("desired angle "+desiredAngle);
//			sh.rotateToDesiredAngle(robotAngleDeg, desiredAngle);
//		}
//
//		if (enemyBlocking) {
//			System.out.println("Enemy is blocking");
//			// TODO do bounce pass
//		}
	}

	private boolean isEnemyBlocking() {
		double enemyAttackerAngle = RobotPlanner.desiredAngle(robotX, robotY,
				enemyAttackerX, enemyAttackerY);
		double attackerAngle = getAttackerAngle();

		return diffInHeadings(enemyAttackerAngle, attackerAngle) < 20;
	}

	private boolean isFacingAttacker() {
		double attackerAngle = getAttackerAngle();
		return diffInHeadings(attackerAngle, robotAngleDeg) < allowedDegreeError;
	}

	private double getAttackerAngle() {
		double attackerAngle = RobotPlanner.desiredAngle(robotX, robotY,
				attackerX, attackerY);
		return attackerAngle;
	}

	private double diffInHeadings(double angleA, double angleB) {
		double diffAngleA, diffAngleB;
		if (Math.abs(360 - angleA) < Math.abs(angleA)) {
			diffAngleA = Math.abs(360 - angleA) * (-1);
		} else {
			diffAngleA = Math.abs(angleA);
		}
		if (Math.abs(360 - angleB) < Math.abs(angleB)) {
			diffAngleB = Math.abs(360 - angleB) * (-1);
		} else {
			diffAngleB = Math.abs(angleB);
		}
		return Math.min(Math.abs(diffAngleA - diffAngleB),
				Math.abs(diffAngleB - diffAngleA));
	}
}
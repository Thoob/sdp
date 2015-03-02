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
	double robotAngleRad;
	public boolean flag = false;
	public int framesPassed = 0;
    
	public void sendWorldState(WorldState worldState) {
		initializeVars(worldState);
	
		int ballzone = RobotPlanner.inZone(ballX, worldState);
		System.out.println("Ballzone is: " + ballzone);
		if (RobotPlanner.inZone(ballX, worldState) != RobotPlanner.inZone(robotX, worldState)){
			System.out.println("Not in same zone");
			RobotCommands.stop();
			return; 
		}
		
		robotAngleRad = Math.toRadians(robotAngleDeg);
		if (robot == null || ball == null)
			return;

		if (flag == false){
			framesPassed = 0;
			sh.acquireBall(worldState);
			if (RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)) {
				flag = true;
				System.out.println("Attempted Catch");
				}	
		} else{ 
			passKick(worldState);
		}
			
	}
		
	public void passKick(WorldState worldState){	

		//TODO fix framesPassed incrementations
		System.out.println("our position " + robotX + " " + robotY + " "
				+ robotAngleDeg);
		System.out.println("attacker position " + attackerX + " " + attackerY
				+ " " + getAttackerAngle());

		// STATE BOOLEANS //
		boolean facingAttacker = isFacingAttacker();
		if (facingAttacker == true /*&& RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX, ballY) */){
			framesPassed++;
			System.out.println("FRAMES PASSED" + framesPassed);
		} else {
		//	framesPassed = 0;
		}
		
		boolean enemyBlocking = isEnemyBlocking();
		
		double AttAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				attackerX, attackerY);
		double ballDist = Math.abs(ballX - robotX) + Math.abs(ballY - robotY);
		//boolean Caught = ballDist < 15;
		
		/* Blocker is not is LoS */
		if (facingAttacker && framesPassed > 20) {
			System.out.println("We are facing Attacker, and have the ball");
			System.out.println("FRAMES PASSED" + framesPassed);
			RobotCommands.passKick();
			flag = false;
			
		} else {
			sh.rotateToDesiredAngle(robotAngleDeg, AttAngleDeg);			
		}
	}

		
	
		
	
	/* BOUNCE PASS */
//		} else {
//			/* Not facing so attempt bounce shot */
//			if (enemyBlocking) {
//				double desiredAngle = Calculations.getBounceAngle(robotX, robotY, Math.toRadians(robotAngleDeg), attackerX, attackerY, enemyAttackerX, enemyAttackerY);
//				System.out.println("desired angle "+desiredAngle);
//				sh.rotateToDesiredAngle(robotAngleDeg, desiredAngle);
//				}
//		}
//



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
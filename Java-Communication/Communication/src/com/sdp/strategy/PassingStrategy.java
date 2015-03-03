package com.sdp.strategy;

import java.awt.geom.Point2D;

import com.sdp.planner.RobotCommands;
import com.sdp.planner.RobotPlanner;
import com.sdp.prediction.Calculations;
import com.sdp.world.SimpleWorldState;
import com.sdp.world.SimpleWorldState.Operation;
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
	public boolean movePass = true;
	public boolean weHaveArrived = false;
	public boolean enemyBlocking = false;
	public int framesPassed = 0;

	public void sendWorldState(WorldState worldState) {
		initializeVars(worldState);


		// Only act when the ball is in our zone. 
		int ballzone = RobotPlanner.inZone(ballX, worldState);
		System.out.println("Ballzone is: " + ballzone);
		if (RobotPlanner.inZone(ballX, worldState) != RobotPlanner.inZone(
				robotX, worldState)) {
			System.out.println("Not in same zone");
			RobotCommands.stop();
			return;
		}

		robotAngleRad = Math.toRadians(robotAngleDeg);
		if (robot == null || ball == null)
			return;

		/*
		 * 'flag' is used to signify if we have attempted a catch - Works under
		 * assumption that we catch it everytime - Set back to 'false' when we
		 * pass to the attacker
		 */

		if (flag == false) {
			framesPassed = 0;
			sh.acquireBall(worldState);
			if (RobotPlanner.doesOurRobotHaveBall(robotX, robotY, ballX, ballY)
					&& RobotPlanner.inZone(ballX, worldState) == RobotPlanner
							.inZone(robotX, worldState)) {
				flag = true;
				System.out.println("Attempted Catch");
			}
		} else {
			passKick(worldState);
		} 

	}

	public void passKick(WorldState worldState) {

		System.out.println("FRAMES PASSED " + framesPassed);
		boolean facingAttacker = isFacingAttacker();
		double AttAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				attackerX, attackerY);
		double faceAttAngleDeg = diffInHeadings(robotAngleDeg, AttAngleDeg);
		double blockerAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				enemyAttackerX, enemyAttackerY);
		boolean doBounce = false;
		
		enemyBlocking = Math.abs((diffInHeadings(robotAngleDeg,
						blockerAngleDeg) - diffInHeadings(robotAngleDeg,
						AttAngleDeg)))  < 20;

		// TODO: Test
		if (facingAttacker
				&& RobotPlanner.inZone(ballX, worldState) == RobotPlanner
					.inZone(robotX, worldState)
				&&  enemyBlocking == false) {
			framesPassed++;
			// When we've been facing the enemy for ~1/2 a second
			if (framesPassed > 10
					&& SimpleWorldState.previousOperation != Operation.PASSKICK) {

				// Discuss angle, and perhaps alternate detection method,
				// we rotate and face our team mate, and if the needed rotation
				// to face the blocker is with in 10 degrees, we assume we are
				// blocked

				if (enemyBlocking != true && doBounce == false) {
					System.out.println("Previous Op: "
							+ SimpleWorldState.previousOperation);

					System.out
							.println("We are facing Attacker, and have the ball");
					RobotCommands.passKick();
					SimpleWorldState.previousOperation = Operation.PASSKICK;

					// Setting flag to false allows us to acquire the ball again
					// when necessary
					movePass = true;
					flag = false;
					return;
				} else
					doBounce = true;
			}
		}

			// Bounce pass is needed
		else if (enemyBlocking == true) {
				moveToPassingPos(worldState);
			}

		else {
			framesPassed = 0;
			sh.rotateToDesiredAngle(robotAngleDeg, AttAngleDeg);
			return;
		}

	}

	/* BOUNCE PASS */
	// } else {
	// /* Not facing so attempt bounce shot */
	// if (enemyBlocking) {
	// double desiredAngle = Calculations.getBounceAngle(robotX, robotY,
	// Math.toRadians(robotAngleDeg), attackerX, attackerY, enemyAttackerX,
	// enemyAttackerY);
	// System.out.println("desired angle "+desiredAngle);
	// sh.rotateToDesiredAngle(robotAngleDeg, desiredAngle);
	// }
	// }
	//

	private boolean isEnemyBlocking() {
		double enemyAttackerAngle = RobotPlanner.desiredAngle(robotX, robotY,
				enemyAttackerX, enemyAttackerY);
		double attackerAngle = getAttackerAngle();

		return diffInHeadings(enemyAttackerAngle, attackerAngle) < 10;
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
	
	private void moveToPassingPos(WorldState worldState){
		
		double attackerPosY = worldState.getAttackerRobot().y;
		double blockerPosY = worldState.getEnemyAttackerRobot().y;
		
		double PassingAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				robotX, attackerPosY);
		double ballDiffInHeadings = Math.abs(robotAngleDeg - PassingAngleDeg);

		boolean isRobotFacingTarget = (ballDiffInHeadings < allowedDegreeError ||
				 			ballDiffInHeadings > 360 - allowedDegreeError);
		 

		// move down or up
		if (Math.abs(attackerPosY - blockerPosY) > 20){
		 // Rotate to target Point
			
			if (isRobotFacingTarget == false) {
				sh.rotateToDesiredAngle(robotAngleDeg, PassingAngleDeg);
				System.out.println("Rotating to face target.");
			}
			
			if (isRobotFacingTarget && weHaveArrived == false){
				RobotCommands.goStraight();
				SimpleWorldState.previousOperation = Operation.NONE;
				System.out.println("Moving towards ball.");
			}
			
			//double deltaX = robotX - ballX;
			double deltaY = robotY - ballY;
			double deltaTotal = Math.abs(deltaY);
			weHaveArrived = deltaTotal < 8;

		}

		
		// in a row
		if (Math.abs(attackerPosY - blockerPosY) < 20){}
		
		// Once we reach our destination
		if (weHaveArrived){
			enemyBlocking = false;
		}

	}
}
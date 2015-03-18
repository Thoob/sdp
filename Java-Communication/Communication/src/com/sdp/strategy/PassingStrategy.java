package com.sdp.strategy;

import com.sdp.planner.RobotCommands;
import com.sdp.planner.RobotPlanner;
import com.sdp.vision.PitchConstants;
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
	public boolean weHaveMoved = false;
	public int framesPassed = 0;

	public void sendWorldState(WorldState worldState) {
		initializeVars(worldState);
		boolean doWeHaveBall = RobotPlanner.doesOurRobotHaveBall(robotX,
				robotY, ballX, ballY);

		System.out.println("we have ball " + doWeHaveBall);
		
		if (RobotPlanner.inZone(ballX, worldState) != RobotPlanner.inZone(
				robotX, worldState)) {
			return;
		}

		robotAngleRad = Math.toRadians(robotAngleDeg);
		if (robot == null || ball == null)
			return;
		
		/*	Commented out, as no way to currently test (should work though, probably better to shove
		 *  in a method)
		int[] topCorner = getTopCorner(worldState);
		int[] lowerTopCorner = getLowerTopCorner(worldState);
		int[] upperBotCorner = getUpperBotCorner(worldState);
		int[] botCorner = getBotCorner(worldState);
		
		double distFromTop = Math.abs(ballY - topOfPitch);
		double distFromBot = Math.abs(ballY - botOfPitch);
		double rightAngX = (worldState.weAreShootingRight) ? ballX + 15 : ballX - 15;

		boolean BallInUpperTri = ballInUpperTri(worldState, ballX, ballY, topCorner[0], lowerTopCorner[1]);
		boolean ballInLowerTri = ballInLowerTri(worldState, ballX, ballY, botCorner[0], upperBotCorner[1]);
		
		
		// TRIs represent the triangles formed at between the y of the inner corner,
		// and the x of the outer corners
		
		// May need to adjust values 
		 if (BallInUpperTri){
			System.out.println("Ball is in upper tri");
			sh.goTo(rightAngX, ballY + 15, worldState);
		}else if (ballInLowerTri){
			System.out.println("Ball is in lower tri");
			sh.goTo(rightAngX, ballY - 15, worldState);
		}else if (distFromTop < 15){
			System.out.println("Ball is near top, adjusting");
			sh.goTo(ballX, ballY + 20, worldState);
		}else if (distFromBot < 15){
			System.out.println("Ball is near bottom, adjusting");
			sh.goTo(ballX,  ballY - 20, worldState);
		} */
		
		
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
				return;
			}
		} else if (flag == true) {
			passKick(worldState);
		}

	}

	public void passKick(WorldState worldState) {

		System.out.println("FRAMES PASSED " + framesPassed);

		boolean facingAttacker = isFacingAttacker();

		double AttAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				attackerX, attackerY);
		double AttDiffInHeadings = Math.abs(robotAngleDeg - AttAngleDeg);
		boolean isFacingAttacker = (AttDiffInHeadings < allowedDegreeError || AttDiffInHeadings > 360 - allowedDegreeError);
		boolean stoppedRotating = (AttDiffInHeadings < 8 || AttDiffInHeadings > 360 - 8);

		double blockerAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				enemyAttackerX, enemyAttackerY);
		enemyBlocking = Math
				.abs((diffInHeadings(robotAngleDeg, blockerAngleDeg) - diffInHeadings(
						robotAngleDeg, AttAngleDeg))) < 10;

		double out = Math
				.abs((diffInHeadings(robotAngleDeg, blockerAngleDeg) - diffInHeadings(
						robotAngleDeg, AttAngleDeg)));

		// TODO: Test
		if (isFacingAttacker
				&& RobotPlanner.inZone(ballX, worldState) == RobotPlanner
						.inZone(robotX, worldState) && enemyBlocking == false) {
			if (stoppedRotating) {
				framesPassed++;
			}
			// When we've been facing the enemy for ~1/2 a second
			if (SimpleWorldState.previousOperation != Operation.PASSKICK
					&& framesPassed > 4) {

				// Discuss angle, and perhaps alternate detection method,
				// we rotate and face our team mate, and if the needed rotation
				// to face the blocker is with in 10 degrees, we assume we are
				// blocked

				if (enemyBlocking != true) {
					System.out.println("Previous Op: "
							+ SimpleWorldState.previousOperation);
					System.out
							.println("We are facing Attacker, and have the ball");
					RobotCommands.passKick();
					SimpleWorldState.previousOperation = Operation.PASSKICK;

					// Setting flag to false allows us to acquire the ball again
					// when necessary
					flag = false;
					return;
				}
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

	private boolean ballInUpperTri(WorldState worldState, double ballX, double ballY,
					int TlX, int lowerTLY){
		if (worldState.weAreShootingRight){
			if ((ballX < TlX) && (ballY < lowerTLY)){
				return true;
			}
		} else {
			if ((ballX > TlX) && (ballY < lowerTLY)){
				return true;
			}
		}
		
		return false;
	}
	
	private boolean ballInLowerTri(WorldState worldState, double ballX, double ballY,
					int BlX, int UpperBLY){
		if (worldState.weAreShootingRight){
			if ((ballX < BlX) && (ballY > UpperBLY)){
				return true;
			}
			} else {
				if ((ballX > BlX) && (ballY > UpperBLY)){
					return true;
				}
			}

		return false;
	}
	
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
	
	private int[] getTopCorner(WorldState worldState){
		if (worldState.weAreShootingRight){
			return PitchConstants.getPitchOutlineTL();
		} else
			return PitchConstants.getPitchOutlineTR();
	}
	
	private int[] getLowerTopCorner(WorldState worldState){
		if (worldState.weAreShootingRight){
			return PitchConstants.getPitchOutlineLowerTL();
		} else
			return PitchConstants.getPitchOutlineLowerTR();
	}
	
	private int[] getBotCorner(WorldState worldState){
		if (worldState.weAreShootingRight){
			return PitchConstants.getPitchOutlineBL();
		} else
			return PitchConstants.getPitchOutlineBR();
	}
	
	private int[] getUpperBotCorner(WorldState worldState){
		if (worldState.weAreShootingRight){
			return PitchConstants.getPitchOutlineUpperBL();
		} else
			return PitchConstants.getPitchOutlineUpperBR();
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

	private void moveToPassingPos(WorldState worldState) {

		double attackerPosY = worldState.getAttackerRobot().y;
		double blockerPosY = worldState.getEnemyAttackerRobot().y;

		double PassingAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				robotX, attackerPosY);
		double ballDiffInHeadings = Math.abs(robotAngleDeg - PassingAngleDeg);

		boolean isRobotFacingTarget = (ballDiffInHeadings < allowedDegreeError || ballDiffInHeadings > 360 - allowedDegreeError);

		// move down or up
		System.out.println("Difference is: "
				+ Math.abs(attackerPosY - blockerPosY));

		if (Math.abs(attackerPosY - blockerPosY) > 35) {
			// Rotate to target Point

			if (isRobotFacingTarget == false) {
				sh.rotateToDesiredAngle(robotAngleDeg, PassingAngleDeg);
				System.out.println("Rotating to face AttackerY");
			}

			if (isRobotFacingTarget && weHaveArrived == false) {
				RobotCommands.goStraight();
				SimpleWorldState.previousOperation = Operation.NONE;
				System.out.println("Moving towards AttackerY.");
			}

			double deltaY = robotY - attackerPosY;
			double deltaTotal = Math.abs(deltaY);
			weHaveArrived = deltaTotal < 8;

		}

		// in a row
		if (Math.abs(attackerPosY - blockerPosY) <= 35) {
			if (attackerPosY > 225) {

				PassingAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
						robotX, 180);
				ballDiffInHeadings = Math.abs(robotAngleDeg - PassingAngleDeg);

				isRobotFacingTarget = (ballDiffInHeadings < allowedDegreeError || ballDiffInHeadings > 360 - allowedDegreeError);

				if (isRobotFacingTarget == false) {
					sh.rotateToDesiredAngle(robotAngleDeg, PassingAngleDeg);
					System.out.println("Rotating to face 180");
				}

				if (isRobotFacingTarget && weHaveArrived == false) {
					RobotCommands.goStraight();
					SimpleWorldState.previousOperation = Operation.NONE;
					System.out.println("Moving towards 180.");
				}

				double deltaY = robotY - 180;
				double deltaTotal = Math.abs(deltaY);
				weHaveArrived = deltaTotal < 20;

			}

			else {
				PassingAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
						robotX, 300);
				ballDiffInHeadings = Math.abs(robotAngleDeg - PassingAngleDeg);

				isRobotFacingTarget = (ballDiffInHeadings < allowedDegreeError || ballDiffInHeadings > 360 - allowedDegreeError);

				if (isRobotFacingTarget == false) {
					sh.rotateToDesiredAngle(robotAngleDeg, PassingAngleDeg);
					System.out.println("Rotating to face target: 300");
				}

				if (isRobotFacingTarget && weHaveArrived == false) {
					RobotCommands.goStraight();
					SimpleWorldState.previousOperation = Operation.NONE;
					System.out.println("Moving towards 300.");
				}

				double deltaY = robotY - 300;
				double deltaTotal = Math.abs(deltaY);
				weHaveArrived = deltaTotal < 20;

			}

		}

		// Once we reach our destination
		if (weHaveArrived) {
			System.out.println("We have arrived");
			enemyBlocking = false;
		}

	}
	
	
	
	// Jordan! The problem was you had (double robotY, double robotX, ...)
	// instead of the other way around; (x, y). - Theo
	public boolean boundaryHelper(double robotX, double robotY,
			WorldState worldState) {

		boolean awayFromTop = Math.abs(robotY - topOfPitch) > 10;
		boolean awayFromBot = Math.abs(robotY - botOfPitch) > 10;

		int[] TRCorner = PitchConstants.getPitchOutlineTR();
		int[] BRCorner = PitchConstants.getPitchOutlineBR();
		int[] TLCorner = PitchConstants.getPitchOutlineTL();

		int[] BLCorner = PitchConstants.getPitchOutlineBL();
		boolean awayFromCornerTop;
		boolean awayFromCornerBot;
		boolean awayFromGoal;

		if (worldState.weAreShootingRight) {

			awayFromCornerTop = Math.abs(robotX - TRCorner[0])
					+ Math.abs(robotY - TRCorner[1]) > 50;

			awayFromCornerBot = Math.abs(robotX - BRCorner[0])
					+ Math.abs(robotY - BRCorner[1]) > 50;

			// RESTRAINT FOR GOAL
			// awayFromGoal = Math.abs(robotX -leftGoalX) < 10;

		} else {

			awayFromCornerTop = Math.abs(robotX - TLCorner[0])
					+ Math.abs(robotY - TLCorner[1]) > 50;

			awayFromCornerBot = Math.abs(robotX - BLCorner[0])
					+ Math.abs(robotY - BLCorner[1]) > 50;

			// RESTRAINT FOR GOAL
			// awayFromGoal = Math.abs(robotX -leftGoalX) < 10
		}
		
		/*
		System.out.println("Distance from Top is: " + Math.abs(robotY - topOfPitch));
		System.out.println("Distance from Bottom is: " + Math.abs(robotY - botOfPitch));
		System.out.println("Distance from Top CORNER is: " + 
				(Math.abs(robotX - TLCorner[0]) + Math.abs(robotY - TLCorner[1])));
		System.out.println("Distance from Bottom CORNER is: " + 
				(Math.abs(robotX - BLCorner[0]) + Math.abs(robotY - BLCorner[1])));
		*/

		if (awayFromBot && awayFromTop && awayFromCornerTop
				&& awayFromCornerBot) {
			return true;
		} else {
			System.out.println("We are at a boundary, adjusting...");
			return false;
		}

	}
}
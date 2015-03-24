package com.sdp.strategy;

import com.sdp.RobotCommunication;
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
	public boolean atBoundaryPos = false;
	public int framesPassed = 0;

	public void sendWorldState(WorldState worldState) {
		initializeVars(worldState);
		boolean doWeHaveBall = RobotPlanner.doesOurRobotHaveBall(
				worldState.ballNotOnPitch, robotX, robotY, ballX, ballY);
		if (doWeHaveBall) {
			System.out.println("we have the ball");
			passKick(worldState);
		} else if (RobotPlanner.inZone(ballX, worldState) == RobotPlanner
				.inZone(robotX, worldState)) {
			System.out.println("we do not have the ball");
			sh.acquireBall(worldState);
		}
	}

	public void adjust() {
		boolean doWeHaveBall = RobotPlanner.doesOurRobotHaveBall(
				worldState.ballNotOnPitch, robotX, robotY, ballX, ballY);

		if (atBoundaryPos == false) {
			System.out.println("we are heading to non collision point");
		}

		if (flag == false) {
			System.out.println("flag is false");
		}

		boolean adjusted = false;
		if (Math.abs(robotY - topOfPitch) < 15
				|| Math.abs(robotY - botOfPitch) < 15) {
			adjusted = false;
		} else
			adjusted = true;

		if (flag == false) {
			framesPassed = 0;
			if (doWeHaveBall == false) {
				sh.acquireBall(worldState);
			}
			if (RobotPlanner.doesOurRobotHaveBall(worldState.ballNotOnPitch,
					robotX, robotY, ballX, ballY)
					&& RobotPlanner.inZone(ballX, worldState) == RobotPlanner
							.inZone(robotX, worldState)) {
				if (adjusted == false) {
					RobotCommunication.getInstance().shortMoveBackwards();
				} else {
					flag = true;
					System.out.println("Attempted Catch, adjusting");
				}
				return;
			}
		}
	}

	public void passKick(WorldState worldState) {

		double attAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				attackerX, attackerY);
		double attDiffInHeadings = Math.abs(robotAngleDeg - attAngleDeg);
		boolean isFacingAttacker = (attDiffInHeadings < 8 || attDiffInHeadings > 360 - 8);

		double blockerAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				enemyAttackerX, enemyAttackerY);
		enemyBlocking = Math
				.abs((diffInHeadings(robotAngleDeg, blockerAngleDeg) - diffInHeadings(
						robotAngleDeg, attAngleDeg))) < 10;

		if (isFacingAttacker && !enemyBlocking) {
			framesPassed++;

			if (SimpleWorldState.previousOperation != Operation.PASSKICK
					&& framesPassed > 2) {
				System.out.println("We are facing Attacker, and have the ball");
				RobotCommands.passKick();
				SimpleWorldState.previousOperation = Operation.PASSKICK;
				isCatcherUp = false;

				return;
			}
		} else {
			framesPassed = 0;
			sh.rotateToDesiredAngle(robotAngleDeg, attAngleDeg);
			return;
		}

		// Bounce pass is needed
		// else if (enemyBlocking == true) {
		// // TODO: Check correctness of BP
		// // Here is where we should do a BP, it only seems to rotate towards
		// // our team mate...//
		// System.out.println("rotating for Bounce Pass");
		// double desiredbounceAngle = Calculations.getBounceAngle(robotX,
		// robotY, Math.toRadians(robotAngleDeg), attackerX,
		// attackerY, enemyAttackerX, enemyAttackerY);
		// System.out.println("desired angle " + desiredbounceAngle);
		// sh.rotateToDesiredAngle(robotAngleDeg, desiredbounceAngle);
		// }
	}

	private boolean ballInUpperTri(WorldState worldState, double ballX,
			double ballY, int TlX, int lowerTLY) {
		if (worldState.weAreShootingRight) {
			if ((ballX < TlX) && (ballY < lowerTLY)) {
				return true;
			}
		} else {
			if ((ballX > TlX) && (ballY < lowerTLY)) {
				return true;
			}
		}

		return false;
	}

	private boolean ballInLowerTri(WorldState worldState, double ballX,
			double ballY, int BlX, int UpperBLY) {
		if (worldState.weAreShootingRight) {
			if ((ballX < BlX) && (ballY > UpperBLY)) {
				return true;
			}
		} else {
			if ((ballX > BlX) && (ballY > UpperBLY)) {
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

	private int[] getTopCorner(WorldState worldState) {
		if (worldState.weAreShootingRight) {
			return PitchConstants.getPitchOutlineTL();
		} else
			return PitchConstants.getPitchOutlineTR();
	}

	private int[] getLowerTopCorner(WorldState worldState) {
		if (worldState.weAreShootingRight) {
			return PitchConstants.getPitchOutlineLowerTL();
		} else
			return PitchConstants.getPitchOutlineLowerTR();
	}

	private int[] getBotCorner(WorldState worldState) {
		if (worldState.weAreShootingRight) {
			return PitchConstants.getPitchOutlineBL();
		} else
			return PitchConstants.getPitchOutlineBR();
	}

	private int[] getUpperBotCorner(WorldState worldState) {
		if (worldState.weAreShootingRight) {
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

	public void boundaryHelper(double robotX, double robotY,
			WorldState worldState) {

		// Commented out, as no way to currently test (should work though,
		// probably better to shove
		// in a method)
		int[] topCorner = getTopCorner(worldState);
		int[] lowerTopCorner = getLowerTopCorner(worldState);
		int[] upperBotCorner = getUpperBotCorner(worldState);
		int[] botCorner = getBotCorner(worldState);

		double distFromTop = Math.abs(ballY - topOfPitch);
		double distFromBot = Math.abs(ballY - botOfPitch);
		double rightAngX = (worldState.weAreShootingRight) ? ballX + 15
				: ballX - 15;

		double distFromTopCor = Math.abs(ballX - topCorner[0]);
		System.out.println("Dist from top corner " + distFromTopCor);

		double distFromTopCorD = Math.abs(ballY - lowerTopCorner[1]);
		System.out.println("Dist from top corner Y " + distFromTopCorD);

		boolean BallInUpperTri = ballInUpperTri(worldState, ballX, ballY,
				topCorner[0], lowerTopCorner[1]);
		boolean ballInLowerTri = ballInLowerTri(worldState, ballX, ballY,
				botCorner[0], upperBotCorner[1]);

		// TRIs represent the triangles formed at between the y of the inner
		// corner,
		// and the x of the outer corners

		// May need to adjust values
		if (BallInUpperTri) {
			System.out.println("Ball is in upper tri");
			sh.goToPoint(rightAngX, ballY + 15, robotX, robotY, worldState);
			if (Math.abs(robotX - rightAngX) + Math.abs(robotY - (ballY + 15)) < 8) {
				atBoundaryPos = true;
			}
		} else if (ballInLowerTri) {
			System.out.println("Ball is in lower tri");
			sh.goToPoint(rightAngX, ballY - 15, robotX, robotY, worldState);
			if (Math.abs(robotX - rightAngX) + Math.abs(robotY - (ballY - 15)) < 8) {
				atBoundaryPos = true;
			}
		} else if (distFromTop < 15) {
			System.out.println("Ball is near top, adjusting");
			sh.goToPoint(ballX, ballY + 20, robotX, robotY, worldState);
			if (Math.abs(robotX - ballX) + Math.abs(robotY - (ballY + 20)) < 8) {
				atBoundaryPos = true;
			}
		} else if (distFromBot < 15) {
			System.out.println("Ball is near bottom, adjusting");
			sh.goToPoint(ballX, ballY - 20, robotX, robotY, worldState);
			if (Math.abs(robotX - ballX) + Math.abs(robotY - (ballY + 20)) < 8) {
				atBoundaryPos = true;
			}
		} else
			atBoundaryPos = true;

	}

}
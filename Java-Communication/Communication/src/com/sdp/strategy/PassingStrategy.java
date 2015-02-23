package com.sdp.strategy;

import java.util.ArrayDeque;
import java.util.Deque;
import com.sdp.world.DynamicWorldState;
import com.sdp.planner.RobotPlanner;
import com.sdp.vision.Vector2f;
import com.sdp.world.WorldState;
import com.sdp.world.DynamicWorldState.Robot;

public class PassingStrategy extends GeneralStrategy {
	private final int allowedDegreeError = 15;

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
	private boolean needReset = false;
	protected double defenderAngleToGoal;
	protected double distFromBall;
	private Deque<Vector2f> ballPositions = new ArrayDeque<Vector2f>();
	private long passTimer;
	private boolean passTimerOn = false;

	public PassingStrategy(){

	}

	public void sendWorldState(DynamicWorldState dynWorldState,
			WorldState worldState) {

		
		// TODO: Test! - somewhat difficulty in recognising 3 robots across the pitch
		//		ROBOT DECLARATIONS		//
		Robot robot = dynWorldState.getDefender();
		Robot AttackingRobot = dynWorldState.getAttacker();
		Robot EnemyDefender = dynWorldState.getEnemyDefender();
		
		//		ROBOT COORDANATES		//
		double robotX = robot.getCenter().getX();
		double robotY = robot.getCenter().getY();
		double AttackerX = AttackingRobot.getCenter().getX();
		double AttackerY = AttackingRobot.getCenter().getY();
		double EnemyDefenderX = EnemyDefender.getCenter().getX();
		double EnemyDefenderY = EnemyDefender.getCenter().getY();

		//		FACING ANGLES			//
		double robotAngleRad = robot.getHeading();
		double robotAngleDeg = Math.toDegrees(robotAngleRad);
		
		double AttackerAngle = RobotPlanner.desiredAngle(robotX, robotY,
				robotAngleRad, AttackerX, AttackerY);
		
		double EnemyDefenderAngle = RobotPlanner.desiredAngle(robotX, robotY,
				robotAngleRad, EnemyDefenderX, EnemyDefenderY);
		
		double DiffInHeadingstoAttacker = Math.abs(robotAngleDeg - AttackerAngle);
		double DiffInHeadingstoBlocker = Math.abs(robotAngleDeg - EnemyDefenderAngle);

		
		//		STATE BOOLEANS			//
		
			/* We are currently facing the attacker */
		boolean facingAttacker = (DiffInHeadingstoAttacker < allowedDegreeError ||
									DiffInHeadingstoAttacker > 360 - allowedDegreeError);
		
			/* The Blocker is currently outwith our facing angle */
		boolean noObstruction = (DiffInHeadingstoBlocker > allowedDegreeError || 
									DiffInHeadingstoBlocker < 360 - allowedDegreeError);
		
			/* We are facing the attacker with no obstruction */
		boolean inLineOfSight = (facingAttacker && noObstruction);
		
		System.out.println("Attacker " + AttackerX + " " + AttackerY + " Heading diff: " + DiffInHeadingstoAttacker);
		System.out.println("Blocker " + EnemyDefenderX + " " + EnemyDefenderY);


		/* DEBUG */
		if (!noObstruction){
			System.out.println("Enemy is blocking");
		}
		if(facingAttacker){
			System.out.println("We are facing Attacker");
		}
		if (inLineOfSight){
			System.out.println("Attacker is in line of sight");
		}

	}
}
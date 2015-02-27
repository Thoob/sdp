package com.sdp.world;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.sdp.prediction.Oracle;

public class WorldState {

	public int[] dividers;
	public float[] leftGoal;
	public float[] rightGoal;
	public boolean attackerNotOnPitch, enemyAttackerNotOnPitch,
			defenderNotOnPitch, enemyDefenderNotOnPitch;
	public boolean ballNotOnPitch;
	private ArrayList<Point2> ballPositionHistory = new ArrayList<Point2>();
	private MovingObject defenderRobot;
	private MovingObject attackerRobot;
	// Enemy team robots
	private MovingObject enemyDefenderRobot;
	private MovingObject enemyAttackerRobot;
	
	private MovingObject ball;
	private Pitch playingField;
	// Flags
	public boolean weAreBlue, weAreShootingRight;
	// Oracle for prediction
	private Oracle predictor = null;

	/**
	 * Added for legacy purposes
	 * */
	public WorldState() {
	}

	/**
	 * Constructor, use it for initial world model initialisation once the play
	 * field data has been assembled
	 */
	public WorldState(Pitch field) {
		this.playingField = field;
		initialiseOracle(field);
	}

	/**
	 * Constructor, use it for initial world model initialisation once the
	 * playing field data has been assembled
	 */
	public WorldState(Pitch field, MovingObject defenderRobot,
			MovingObject attackerRobot, MovingObject enemyDefenderRobot,
			MovingObject enemyAttackerRobot, MovingObject ball) {
		this.playingField = field;
		this.defenderRobot = defenderRobot;
		this.attackerRobot = attackerRobot;
		this.enemyAttackerRobot = enemyAttackerRobot;
		this.enemyDefenderRobot = enemyDefenderRobot;
		this.ball = ball;
		initialiseOracle(field);
	}

	// get methods
	/**
	 * Returns the enemy defender robot object
	 */
	public MovingObject getEnemyDefenderRobot() {
		return this.enemyDefenderRobot;
	}

	/**
	 * Returns the enemy attacker robot object
	 */
	public MovingObject getEnemyAttackerRobot() {
		return this.enemyAttackerRobot;
	}

	/**
	 * Returns the defender robot object
	 */
	public MovingObject getDefenderRobot() {
		return this.defenderRobot;
	}

	/**
	 * Returns the attacker robot object returns null if the model is locked
	 */
	public MovingObject getAttackerRobot() {
		return this.attackerRobot;
	}

	/**
	 * Returns the ball object
	 */
	public MovingObject getBall() {
		return this.ball;
	}

	/**
	 * Returns the pitch object
	 */
	public Pitch getPitch() {
		return this.playingField;
	}

	public boolean getPossession() {
		if (Math.abs(attackerRobot.x - ball.x) < 50
				&& Math.abs(attackerRobot.y - ball.y) < 50) {
			return true;
		} else if (Math.abs(defenderRobot.x - ball.x) < 50
				&& Math.abs(defenderRobot.y - ball.y) < 50) {
			return true;
		}
		return false;
	}

	// update methods
	/**
	 * Updates the field with data for moving objects: the robots and the ball
	 */
	public void updateField(MovingObject enemyAttackerRobot,
			MovingObject enemyDefenderRobot, MovingObject attackerRobot,
			MovingObject defenderRobot, MovingObject ball) {
		// the actual update
		this.defenderRobot = defenderRobot;
		this.attackerRobot = attackerRobot;
		this.enemyAttackerRobot = enemyAttackerRobot;
		this.enemyDefenderRobot = enemyDefenderRobot;
		this.ball = ball;
	}

	/**
	 * Updates the ball object
	 * */
	public void setBall(MovingObject ball) {
		this.ball = ball;
	}

	/**
	 * Updates the enemy attacker robot object
	 * */
	public void setEnemyAttackerRobot(MovingObject enemyAttackerRobot) {
		this.enemyAttackerRobot = enemyAttackerRobot;
	}

	/**
	 * Updates the enemy defender robot object
	 * */
	public void setEnemyDefenderRobot(MovingObject enemyDefenderRobot) {
		this.enemyDefenderRobot = enemyDefenderRobot;
	}

	/**
	 * Updates the attacker robot object
	 * */
	public void setAttackerRobot(MovingObject attackerRobot) {
		this.attackerRobot = attackerRobot;
	}

	/**
	 * Updates the defender robot object
	 * */
	public void setDefenderRobot(MovingObject defenderRobot) {
		this.defenderRobot = defenderRobot;
	}

	public ArrayList<Point2> getBallPositionHistory() {
		return ballPositionHistory;
	}

	public void setBallPositionHistory(ArrayList<Point2> ballPositionHistory) {
		this.ballPositionHistory = ballPositionHistory;
	}

	public void updateBallPositionHistory(Point2D ballPosition) {
		Point2 ballToPoint = new Point2((float) ballPosition.getX(),
				(float) ballPosition.getY());
		this.ballPositionHistory.add(ballToPoint);
		if (this.ballPositionHistory.size() > 5)
			this.ballPositionHistory.remove(0);
	}

	// methods for implementation of the prediction system with the world state
	public MovingObject predictNextState(int framesForward) {
		Point2 prediction = this.predictor.predictState(ballPositionHistory,
				framesForward);
		MovingObject nextState = new MovingObject(prediction.getX(),
				prediction.getY());
		return nextState;
	}

	private void initialiseOracle(Pitch field) {
		// Needs to be changed to use constants for top, bottom, left, right
		// from
		// the pitch object, I can't find them.
		this.predictor = new Oracle(300, 300, 600, 600);
	}
}
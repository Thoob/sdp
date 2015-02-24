package com.sdp.strategy;

import java.beans.PropertyChangeSupport;

import com.sdp.vision.interfaces.WorldStateReceiver;
import com.sdp.world.WorldState;

public class StrategyController implements WorldStateReceiver {

	public enum StrategyType {
		DO_NOTHING, PASSING, ATTACKING, DEFENDING, MARKING
	}

	public enum BallLocation {
		DEFENDER, ATTACKER, ENEMY_DEFENDER, ENEMY_ATTACKER
	}

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private BallLocation ballLocation;
	private StrategyType currentStrategy = StrategyType.DO_NOTHING;
	private boolean pauseStrategyController = true;

	// Advanced Tactics flags
	public static boolean confusionEnabled = false;
	public static boolean bouncePassEnabled = false;

	public StrategyController() {

	}

	public StrategyType getCurrentStrategy() {
		return currentStrategy;
	}

	public void changeToStrategy(StrategyType type) {
		switch (type) {
		case DO_NOTHING:
			break;
		case PASSING:
			break;
		case ATTACKING:
			break;
		case DEFENDING:
			break;
		case MARKING:
			break;
		default:
			break;
		}
		StrategyType oldType = currentStrategy;
		currentStrategy = type;
		pcs.firePropertyChange("currentStrategy", oldType, currentStrategy);
	}

	/*
	 * TODO 
	 * We start with defender strategy by default which tries to save the goal
	 * and block ball and acquire it
	 * 
	 * Once we have the ball we either choose passing or attacking strategy
	 */
	@Override
	public void sendWorldState(WorldState worldState) {
		if (pauseStrategyController)
			return;
		// Check where the ball is, and make a decision on which strategies to
		// run based upon that.
		int defenderCheck = (worldState.weAreShootingRight) ? worldState.dividers[0]
				: worldState.dividers[2];
		int leftCheck = (worldState.weAreShootingRight) ? worldState.dividers[1]
				: worldState.dividers[0];
		int rightCheck = (worldState.weAreShootingRight) ? worldState.dividers[2]
				: worldState.dividers[1];
		float ballX = worldState.getBall().x;
		// Mark zone the ball was in on the previous frame.
		BallLocation prevBallLocation = this.ballLocation;

		// Find where the ball is located on the pitch
		if ((worldState.weAreShootingRight && ballX < defenderCheck)
				|| (!worldState.weAreShootingRight && ballX > defenderCheck)) {
			this.ballLocation = BallLocation.DEFENDER;
		} else if (ballX > leftCheck && ballX < rightCheck) {
			this.ballLocation = BallLocation.ATTACKER;
		} else if (worldState.weAreShootingRight && ballX > defenderCheck
				&& ballX < leftCheck || !worldState.weAreShootingRight
				&& ballX < defenderCheck && ballX > rightCheck) {
			this.ballLocation = BallLocation.ENEMY_ATTACKER;
		} else if (!worldState.weAreShootingRight && (ballX < leftCheck)
				|| worldState.weAreShootingRight && (ballX > rightCheck)) {
			this.ballLocation = BallLocation.ENEMY_DEFENDER;
		}

		// Change strategy only if the ball has changed pitch area.
		if (prevBallLocation != ballLocation) {
			switch (this.ballLocation) {
			case ATTACKER:
				changeToStrategy(StrategyType.ATTACKING);
				break;
			case DEFENDER:
				changeToStrategy(StrategyType.PASSING);
				break;
			case ENEMY_ATTACKER:
				changeToStrategy(StrategyType.DEFENDING);
				break;
			case ENEMY_DEFENDER:
				changeToStrategy(StrategyType.MARKING);
				break;
			}
		}
	}

	public boolean isPaused() {
		return pauseStrategyController;
	}

	public void setPaused(boolean b) {
		pauseStrategyController = b;
	}
}
package com.sdp.strategy;

import java.beans.PropertyChangeSupport;

import com.sdp.vision.interfaces.WorldStateReceiver;
import com.sdp.world.WorldState;

public class StrategyController implements WorldStateReceiver {

	public enum StrategyType {
		DO_NOTHING, PASSING, ATTACKING, DEFENDING,
	}

	public enum BallLocation {
		DEFENDER, ATTACKER, ENEMY_DEFENDER, ENEMY_ATTACKER
	}

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private BallLocation ballLocation;
	private static StrategyType currentStrategy = StrategyType.DO_NOTHING;
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
		default:
			break;
		}
		StrategyType oldType = currentStrategy;
		currentStrategy = type;
		pcs.firePropertyChange("currentStrategy", oldType, currentStrategy);
	}

	@Override
	public void sendWorldState(WorldState worldState) {
		if (pauseStrategyController)
			return;
		// Mark zone the ball was in on the previous frame.
		BallLocation prevBallLocation = this.ballLocation;

		// Find where the ball is located on the pitch
		findBallLocation(worldState);

		// Change strategy only if the ball has changed pitch area.
		if (prevBallLocation != ballLocation) {
			switch (this.ballLocation) {
			case DEFENDER:
				// TODO maybe add attacking in some cases?
				changeToStrategy(StrategyType.PASSING);
				break;
			case ENEMY_ATTACKER:
				changeToStrategy(StrategyType.DEFENDING);
				break;
			default:
				break;
			}
		}
	}

	private void findBallLocation(WorldState worldState) {
		// Check where the ball is, and make a decision on which strategies to
		// run based upon that.
		int defenderCheck = (worldState.weAreShootingRight) ? worldState.dividers[0]
				: worldState.dividers[2];
		int leftCheck = (worldState.weAreShootingRight) ? worldState.dividers[1]
				: worldState.dividers[0];
		int rightCheck = (worldState.weAreShootingRight) ? worldState.dividers[2]
				: worldState.dividers[1];
		if(worldState.getBall()==null){
			System.out.println("No ball on pitch");
			return;
		}
		float ballX = worldState.getBall().x;

		if ((worldState.weAreShootingRight && ballX < defenderCheck)
				|| (!worldState.weAreShootingRight && ballX > defenderCheck)) {
			this.ballLocation = BallLocation.DEFENDER;
			System.out.println("DEFENDER");
		} else if (ballX > leftCheck && ballX < rightCheck) {
			this.ballLocation = BallLocation.ATTACKER;
			System.out.println("ATACKER");
		} else if (worldState.weAreShootingRight && ballX > defenderCheck
				&& ballX < leftCheck || !worldState.weAreShootingRight
				&& ballX < defenderCheck && ballX > rightCheck) {
			this.ballLocation = BallLocation.ENEMY_ATTACKER;
			System.out.println("E_ATACKER");
		} else if (!worldState.weAreShootingRight && (ballX < leftCheck)
				|| worldState.weAreShootingRight && (ballX > rightCheck)) {
			this.ballLocation = BallLocation.ENEMY_DEFENDER;
			System.out.println("E_DEFENDER");			
		}
	}

	public boolean isPaused() {
		return pauseStrategyController;
	}

	public void setPaused(boolean b) {
		pauseStrategyController = b;
	}
}
package com.sdp.strategy;

import java.util.ArrayDeque;
import java.util.Deque;

import com.sdp.vision.Vector2f;
import com.sdp.world.WorldState;


public class PassingStrategy extends GeneralStrategy {
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

	@Override
	public void sendWorldState(WorldState worldState) {
		super.sendWorldState(worldState);

	}
}
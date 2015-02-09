package com.sdp.strategy;

import com.sdp.Constants;
import com.sdp.vision.PitchConstants;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.MovingObject;
import com.sdp.world.WorldState;

public class SimpleGeneralStrategy {
	protected static float goalX;
	protected static float[] goalY;
	protected static float ourGoalX;
	protected static float[] ourGoalY;
	protected static float[] ourGoalEdges = new float[3];
	protected static int topOfPitch;
	protected static int botOfPitch;

	public void sendWorldState(WorldState worldState) {

		topOfPitch = PitchConstants.getPitchOutlineTop();
		botOfPitch = PitchConstants.getPitchOutlineBottom();
		if (Constants.areWeShootingRight) {
			goalX = 640;
			ourGoalX = PitchConstants.getPitchOutline()[7].getX();
			goalY = worldState.rightGoal;
			ourGoalEdges[0] = PitchConstants.getPitchOutline()[7].getY();
			ourGoalEdges[1] = worldState.leftGoal[1];
			ourGoalEdges[2] = PitchConstants.getPitchOutline()[6].getY();
			ourGoalY = worldState.leftGoal;
		} else {
			goalX = 0;
			ourGoalX = PitchConstants.getPitchOutline()[2].getX();
			goalY = worldState.leftGoal;
			ourGoalEdges[0] = PitchConstants.getPitchOutline()[2].getY();
			ourGoalEdges[1] = worldState.rightGoal[1];
			ourGoalEdges[2] = PitchConstants.getPitchOutline()[3].getY();
			ourGoalY = worldState.rightGoal;
		}
	}
}

package com.sdp.strategy;

import com.sdp.Constants;
import com.sdp.vision.PitchConstants;
import com.sdp.vision.Position;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.MovingObject;
import com.sdp.world.WorldState;

public class SimpleGeneralStrategy {

	// TODO dirty solution for milestone 2
	// center coordinates
	protected static float leftGoalBotX = -606;
	protected static float leftGoalBotY = 132;
	protected static float leftGoalTopX = -601;
	protected static float leftGoalTopY = -229;

	protected static float leftGoalX = -572;
	protected static float leftGoalY = -62;
	protected static float rightGoalX = 575;
	protected static float rightGoalY = -52;

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
			goalX = PitchConstants.getPitchOutline()[3].getX();
			goalY = worldState.rightGoal;

			ourGoalX = PitchConstants.getPitchOutline()[7].getX();
			ourGoalY = worldState.leftGoal;

			ourGoalEdges[0] = PitchConstants.getPitchOutline()[7].getY();
			ourGoalEdges[1] = worldState.leftGoal[1];
			ourGoalEdges[2] = PitchConstants.getPitchOutline()[6].getY();
		} else {
			goalX = PitchConstants.getPitchOutline()[7].getX();
			goalY = worldState.leftGoal;

			ourGoalX = PitchConstants.getPitchOutline()[3].getX();
			ourGoalY = worldState.rightGoal;

			Position[] tmp = PitchConstants.getPitchOutline();

			ourGoalEdges[0] = PitchConstants.getPitchOutline()[2].getY();
			ourGoalEdges[1] = worldState.rightGoal[1];
			ourGoalEdges[2] = PitchConstants.getPitchOutline()[3].getY();
		}
	}
}

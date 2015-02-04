/**
 * Name: Oracle.java
 * Author: Dimitar Petrov
 * Description: Predicts the next state of a point, based on a history of the points movements.
 * The prediction algorithm needs information for at least 4 points to make a prediction
 * possibly implement a prediction scheme for 4 or less points
 * **/

package com.sdp.prediction;

import java.util.ArrayList;

import com.sdp.prediction.Calculations.CorrectionType;
import com.sdp.world.Point2;

public class Oracle {
	private int boundaryTop;
	private int boundaryBottom;
	private int boundaryLeft;
	private int boundaryRight;
	
	public Oracle(int top_b, int bottom_b, int left_b, int right_b){
		this.boundaryBottom = bottom_b;
		this.boundaryLeft = left_b;
		this.boundaryRight = right_b;
		this.boundaryTop = top_b;		
	}
	
	/**
	 * Returns the coordinates of the predicted state of a point.
	 * Predictions several states forward will be more inaccurate.
	 * TODO: Change frames_forward to time in ms
	 * TODO: Implement boundary calculations and detections for the corners
	 * TODO: Implement a check to see if a point is within the Pitch (use pitch.getBoundPolygon somehow?)
	 * */
	public Point2 PredictState(ArrayList<Point2> history, int frames_forward){
		if(frames_forward < 0)
			throw new IllegalArgumentException("frames_forward cannot be a negative value: "+frames_forward);
		else{
			Point2 prediction = null;
			while(frames_forward > 0){
				frames_forward--;
				//get future point
				prediction = Calculations.predictNextPoint(history);
				//check for boundary violation
				boolean boundaryCheck = false;
				
				if(boundaryCheck){
					
					//TOP violation
					if(prediction.getY() > boundaryTop)
						prediction = Calculations.calculateBounceCoordinate(prediction, CorrectionType.TOP_OR_BOTTOM, boundaryTop);
					//Bottom violation
					else if(prediction.getY() < boundaryBottom)
						prediction = Calculations.calculateBounceCoordinate(prediction, CorrectionType.TOP_OR_BOTTOM, boundaryBottom);
					//LEFT violation
					else if(prediction.getX() < boundaryLeft)
						prediction = Calculations.calculateBounceCoordinate(prediction, CorrectionType.LEFT_OR_RIGHT, boundaryLeft);
					//Right violation
					else if(prediction.getY() > boundaryRight)
						prediction = Calculations.calculateBounceCoordinate(prediction, CorrectionType.LEFT_OR_RIGHT, boundaryRight);
					
				}
				//add to history
				history.add(prediction);
			}
			
			return prediction;
		}
	}
		
		public void setBoundaries(int top, int bottom, int left, int right){
			this.boundaryBottom = bottom;
			this.boundaryLeft = left;
			this.boundaryRight = right;
			this.boundaryTop = top;		
		}	
	}
	


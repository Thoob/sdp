package com.sdp.world;

import java.awt.geom.Point2D;

public class MovingObject implements Cloneable {
	// x,y, representation on the grid
	// x,y are mm representations
	public float x;
	public float y;

	public double velocity;

	// Orientation coordinates
	public float orientationAngle;

	/**
	 * Initializes a moving object
	 * 
	 * @param x
	 *            represents the X coordinate
	 * @param y
	 *            represents the Y coordinate
	 * @param angle
	 *            represents the orientation angle of the object
	 * */
	public MovingObject(float x, float y, float angle) {
		this.x = x;
		this.y = y;
		this.orientationAngle = angle;
	}

	/**
	 * Initializes a moving object
	 * 
	 * @param x
	 *            represents the X coordinate
	 * @param y
	 *            represents the Y coordinate
	 * */
	public MovingObject(float x, float y) {
		this.x = x;
		this.y = y;
	}

	// rotating point of robots, need to represent
	// robot dimension extension from plates

	public Point2D asPoint() {
		return new Point2D.Double(x, y);
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (Exception e) {
			return null;
		}
	}
}
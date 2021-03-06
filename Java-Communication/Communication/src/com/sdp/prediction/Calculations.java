package com.sdp.prediction;

import java.util.ArrayList;

import com.sdp.planner.RobotPlanner;
import com.sdp.vision.PitchConstants;
import com.sdp.world.Point2;

public final class Calculations {

	public enum CorrectionType {
		TOP_OR_BOTTOM, LEFT_OR_RIGHT,
	}

	/**
	 * @return returns the distance between 2 points in a 2D plane
	 * */
	public static float getDistance(Point2 a, Point2 b) {
		double x1, x2, y1, y2;
		x1 = (double) a.getX();
		x2 = (double) b.getX();
		y1 = (double) a.getY();
		y2 = (double) b.getY();

		double distance = Math.sqrt(Math.pow((y1 - y2), 2)
				+ Math.pow((x1 - x2), 2));
		return (float) distance;
	}

	/**
	 * returns the slope of a line determined by two points
	 * */
	public static float getSlopeOfLine(Point2 a, Point2 b) {
		return (a.getX() - b.getY()) / (a.getY() - b.getY());
	}

	/**
	 * [0] is the distance in moment t (current time - 1) [1] is the distance in
	 * moment t-1 (current time - 2) [2] is the distance in moment t-2 (current
	 * time - 3)
	 * */
	// ???
	public static float linearPrediction(float[] data) {
		float v1, v2, v3, a1_2, a2_3, acc_decay;
		v1 = data[data.length - 1];
		v2 = data[data.length - 2];
		v3 = data[data.length - 3];
		a1_2 = Math.abs(v2 - v1);
		a2_3 = Math.abs(v3 - v2);
		acc_decay = a2_3 - a1_2;
		if (v1 - (a1_2 - acc_decay) > 0)
			return v1 - (a1_2 - acc_decay);
		else
			return 0;
	}

	/**
	 * This method will calculate the coordinates of the end point, if its
	 * trajectory indicates it will bounce off a wall
	 * */
	// TODO: implement support for the corners
	public static Point2 calculateBounceCoordinate(Point2 prediction,
			CorrectionType type, float boundary) {
		float predictionX = prediction.getX(), predictionY = prediction.getY();
		if (type == CorrectionType.TOP_OR_BOTTOM) {
			float changeY = Math.abs(predictionY - boundary);
			float correctionY = Math.abs(predictionY - 2 * changeY);
			Point2 correctionPoint = new Point2(predictionX, correctionY);
			return correctionPoint;
		} else if (type == CorrectionType.LEFT_OR_RIGHT) {
			float changeX = Math.abs(boundary - predictionX);
			float correctionX = Math.abs(predictionX - 2 * changeX);
			Point2 correction = new Point2(correctionX, predictionY);
			return correction;
		}
		return null;
	}

	public static Point2 getPointViaDistance(float distance, Point2 a, Point2 b) {
		// odd case where a == b
		if (a.getX() == b.getX() && a.getY() == b.getY())
			return a;

		float dist = getDistance(a, b);
		float sinA = Math.abs(a.getY() - b.getY()) / dist;
		float cosA = Math.abs(a.getX() - b.getX()) / dist;

		float x = b.getX() + cosA * distance;
		float y = b.getY() + sinA * distance;

		Point2 pred = new Point2(x, y);
		return pred;
	}

	public static Point2 predictNextPoint(ArrayList<Point2> history) {
		if (history.size() == 0)
			return new Point2(0, 0);
		if (history.size() < 4) {

			double orientation = 0.785;
			// initial kick multiplier. Needs to be adjusted
			double multiplier = 3;
			double tg = Math.tan(orientation);
			float X0 = history.get(0).getX();
			float Y0 = history.get(0).getY();
			double b = X0 - Y0 / tg;
			// new point
			double X1, Y1;
			Y1 = Y0 * multiplier;
			X1 = b + Y1 / tg;

			Point2 prediction = new Point2((float) X1, (float) Y1);
			return prediction;
		} else {
			int size = history.size();
			// compute distance travelled for the last 4 points
			float[] distances = new float[4];
			distances[0] = getDistance(history.get(size - 4),
					history.get(size - 3));
			distances[1] = getDistance(history.get(size - 3),
					history.get(size - 2));
			distances[2] = getDistance(history.get(size - 2),
					history.get(size - 1));
			// Get predicted distance
			float prediction = linearPrediction(distances);

			Point2 pred = getPointViaDistance(prediction,
					history.get(size - 2), history.get(size - 1));

			return pred;
		}
	}

	/**
	 * returns the angle from which a bounce shot can be scored, from the
	 * position supplied
	 * 
	 * @param bounceDirection
	 *            0 for automatic, 1 for top, -1 for bottom bounce
	 * */
	public static float getBounceAngle(double robotX, double robotY,
			double robotOrientation, double targetX, double targetY,
			double blockerX, double blockerY) {

		double robotRad = Math.toRadians(robotOrientation);
		if (robotRad > Math.PI)
			robotRad -= 2 * Math.PI;

		double x1 = (double) robotX;
		double y1 = (double) robotY;
		double x2 = (double) targetX;
		double y2 = (double) targetY;

		int bottomBoundary = PitchConstants.getPitchOutlineBottom();
		int topBoundary = PitchConstants.getPitchOutlineTop();
		int middleBoundary = (bottomBoundary + topBoundary) / 2;
		double y3;

		if (blockerY > middleBoundary) {
			y3 = topBoundary - 50;
		} else {
			y3 = bottomBoundary + 20;
		}
		System.out.println("milddleBoundary = " + middleBoundary + " blY "
				+ blockerY + " y3 " + y3);

		double a = Math.abs(y3 - y2);
		double b = Math.abs(y3 - y1);

		double c = Math.abs(x1 - x2);

		// calculate the coordinates of the middle point
		double middle = (c * b) / (a + b);

		double deltaA = c - middle;
		double deltaB = middle;

		double x3;
		if (x2 > x1) // shooting right
			x3 = x1 + deltaB;
		else
			x3 = x2 + deltaA;

		double choiceAngle = RobotPlanner.desiredAngle(robotX, robotY, x3,
				y3);

		return (float) choiceAngle;
	}
}

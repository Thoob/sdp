package com.sdp.vision.recognisers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.sdp.vision.DistortionFix;
import com.sdp.vision.PitchConstants;
import com.sdp.vision.PixelInfo;
import com.sdp.vision.Position;
import com.sdp.vision.Vector2f;
import com.sdp.vision.Vision;
import com.sdp.vision.interfaces.ObjectRecogniser;
import com.sdp.vision.interfaces.PitchViewProvider;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.MovingObject;
import com.sdp.world.Pitch;
import com.sdp.world.StaticWorldState;
import com.sdp.world.WorldState;

public class BallRecogniser implements ObjectRecogniser {
	private Pitch pitch;
	private Vision vision;
	private WorldState worldState;
	private PitchConstants pitchConstants;
	private DistortionFix distortionFix;
	private Vector2f previousBallPosition = new Vector2f(0, 0);

	public BallRecogniser(Vision vision, WorldState worldState,
			PitchConstants pitchConstants, DistortionFix distortionFix,
			Pitch pitch) {
		this.pitch = pitch;
		this.vision = vision;
		this.worldState = worldState;
		this.pitchConstants = pitchConstants;
		this.distortionFix = distortionFix;
	}

	@Override
	public void processFrame(PixelInfo[][] pixels, BufferedImage frame,
			Graphics2D debugGraphics, BufferedImage debugOverlay,
			StaticWorldState result) {
		ArrayList<Position> ballPoints = new ArrayList<Position>();
		int top = this.pitchConstants.getPitchTop();
		int left = this.pitchConstants.getPitchLeft();
		int right = left + this.pitchConstants.getPitchWidth();
		int bottom = top + this.pitchConstants.getPitchHeight();

		for (int row = top; row < bottom; row++) {
			for (int column = left; column < right; column++) {
				if (pixels[column][row] != null) {
					if (vision.isColour(pixels[column][row],
							PitchConstants.OBJECT_BALL)) {
						ballPoints.add(new Position(column, row));
						if (this.pitchConstants
								.debugMode(PitchConstants.OBJECT_BALL)) {
							debugOverlay.setRGB(column, row, 0xFF000000);
						}
					}
				}
			}
		}

		Vector2f ballPosition = vision.calculatePosition(ballPoints);
		if (ballPosition.x != 0 || ballPosition.y != 0) {
			debugGraphics.setColor(Color.red);
			debugGraphics.drawLine(0, (int) ballPosition.y, 640,
					(int) ballPosition.y);
			debugGraphics.drawLine((int) ballPosition.x, 0, (int) ballPosition.x,
					480);
		}

		worldState.ballNotOnPitch = false;
		
		if (ballPosition.x == 0 && ballPosition.y == 0) {
			ballPosition = previousBallPosition;
			worldState.ballNotOnPitch = true;
		} else {
			// Distortion fixing
			Point2D.Double point = new Point2D.Double(ballPosition.x,
					ballPosition.y);
			distortionFix.barrelCorrect(point);
			ballPosition.x = (float) point.x;
			ballPosition.y = (float) point.y;
			
			previousBallPosition = ballPosition;
		}
		
		MovingObject ball = new MovingObject(ballPosition.x, ballPosition.y);
		worldState.setBall(ball);
		
		MovingObject ballPredPos = worldState.predictNextState(1);
		worldState.updateBallPositionHistory(ballPredPos.asPoint());

		Point2D position = new Point2D.Double(ballPosition.x, ballPosition.y);
		pitch.framePointToModel(position);
		result.setBall(new Point((int) position.getX(), (int) position.getY()));
	}

	public static class ViewProvider implements PitchViewProvider {
		private DynamicWorldState dynamicWorldState;
		private Pitch pitch;
		
		public ViewProvider(DynamicWorldState dynamicWorldState, Pitch pitch) {
			this.dynamicWorldState = dynamicWorldState;
			this.pitch = pitch;
		}
		
		@Override
		public void drawOnPitch(Graphics2D g) {
			Shape ball = dynamicWorldState.getBall().getShape();
			if (ball != null) {
				g.setColor(Color.RED);
				g.fill(ball);
			}
		}
	}

	@Override
	public void processFrame(PixelInfo[][] pixels, BufferedImage frame,
			Graphics2D debugGraphics, BufferedImage debugOverlay,
			com.sdp.vision.interfaces.StaticWorldState staticWorldState) {
	}
}

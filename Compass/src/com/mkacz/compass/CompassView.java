package com.mkacz.compass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;

/*
 * A simple view that displays compass needle and labels of places in directions
 * based on their geographic location and the location of the user.
 */
public class CompassView extends View
{
	/*
	 * Some constants defining appearance. Could be modable but let's not
	 * complicate it.
	 */
	private static final float RADIUS_FACTOR = 0.45f;
	private static final int CIRCLE_COLOR = 0xFF8A8A8A;
	private static final float NEEDLE_WIDTH = 0.1f;
	private static final float NEEDLE_LENGTH = 0.7f;
	private static final int NEEDLE_NORTH_COLOR = 0xFFDB1237;
	private static final int NEEDLE_NORTH_SHADOW_COLOR = 0xFF990C26;
	private static final int NEEDLE_SOUTH_COLOR = 0xFFE3F4FC;
	private static final int NEEDLE_SOUTH_SHADOW_COLOR = 0xFF7E888C;
	
	/*
	 * Objects used for drawing.
	 */
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Path path = new Path();
	
	/*
	 * Coordinate system.
	 */
	private float radius = 1;
	private float[] center = new float[] {0, 0};
	private float[] north = new float[] {0, -1};
	private float[] east = new float[] {1, 0};
	
	/*
	 * Primitives to be drawn.
	 */
	private RectF border = new RectF();
	private Rect[] placeRects = null;
	private String[] placeNames = null;
	
	public CompassView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		// Draw border oval.
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(CIRCLE_COLOR);
		canvas.drawOval(border, paint);
		
		// Draw needle.
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(NEEDLE_NORTH_COLOR);
		path.reset();
		path.lineTo(NEEDLE_WIDTH * east[0], NEEDLE_WIDTH * east[1]);
		path.lineTo(NEEDLE_LENGTH * north[0], NEEDLE_LENGTH * north[1]);
		path.close();
		path.offset(center[0], center[1]);
		canvas.drawPath(path, paint);
		
		paint.setColor(NEEDLE_NORTH_SHADOW_COLOR);
		path.reset();
		path.lineTo(-NEEDLE_WIDTH * east[0], -NEEDLE_WIDTH * east[1]);
		path.lineTo(NEEDLE_LENGTH * north[0], NEEDLE_LENGTH * north[1]);
		path.close();
		path.offset(center[0], center[1]);
		canvas.drawPath(path, paint);
		
		paint.setColor(NEEDLE_SOUTH_COLOR);
		path.reset();
		path.lineTo(-NEEDLE_WIDTH * east[0], -NEEDLE_WIDTH * east[1]);
		path.lineTo(-NEEDLE_LENGTH * north[0], -NEEDLE_LENGTH * north[1]);
		path.close();
		path.offset(center[0], center[1]);
		canvas.drawPath(path, paint);
		
		paint.setColor(NEEDLE_SOUTH_SHADOW_COLOR);
		path.reset();
		path.lineTo(NEEDLE_WIDTH * east[0], NEEDLE_WIDTH * east[1]);
		path.lineTo(-NEEDLE_LENGTH * north[0], -NEEDLE_LENGTH * north[1]);
		path.close();
		path.offset(center[0], center[1]);
		canvas.drawPath(path, paint);
		
		// Draw places.
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		float oldRadius = Math.min(oldw, oldh) * RADIUS_FACTOR;
		if (oldRadius == 0)
			oldRadius = 1;
		radius = Math.min(w, h) * RADIUS_FACTOR;
		center[0] = w / 2;
		center[1] = h / 2;
		east[0] *= radius / oldRadius;
		east[1] *= radius / oldRadius;
		north[0] *= radius / oldRadius;
		north[1] *= radius / oldRadius;
		border.set(-radius, -radius, radius, radius);
		border.offset(center[0], center[1]);
	}
	
	/*
	 * Computes local coordinate system given the rotation matrix that
	 * transforms the phone coordinates into world coordinates.
	 * 
	 * See SensorManager.getRotationMarix().
	 */
	void setRotationMatrix(float[] rot)
	{
		// Assign projected world directions to be local directions
		east[0] = rot[0];
		east[1] = rot[1];
		north[0] = rot[3];
		north[1] = rot[4];

		float orientation = 1;
		// If the phone is held screen to ground, flip handedness of the
		// coordinate system.
		if (-rot[8] > Math.abs(rot[6]) && -rot[8] > Math.abs(rot[7]))
			orientation = -1;
		
		// Choose the longer projection and determine the other one with respect
		// to it.
		float northLenSq = north[0] * north[0] + north[1] * north[1];
		float eastLenSq = east[0] * east[0] + east[1] * east[1];
		if (eastLenSq > northLenSq)
		{
			float len = FloatMath.sqrt(eastLenSq);
			east[0] /= len;
			east[1] /= len;
			north[0] = -orientation * east[1];
			north[1] = orientation * east[0];
		}
		else
		{
			float len = FloatMath.sqrt(northLenSq);
			north[0] /= len;
			north[1] /= len;
			east[0] = orientation * north[1];
			east[1] = -orientation * north[0];
		}
		
		// Flip the y-component since the canvas coordinate system is y-down.
		east[1] = -east[1];
		north[1] = -north[1];
		
		// Adjust the length of the unit vectors to be radius.
		east[0] *= radius;
		east[1] *= radius;
		north[0] *= radius;
		north[1] *= radius;
		         
		invalidate();
	}
}

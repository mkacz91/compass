package com.mkacz.compass;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;

import com.mkacz.vector_math.Float2;
import com.mkacz.vector_math.Float3;

/*
 * A simple view that displays compass needle and labels of places in directions
 * based on their geographic location and the location of the user.
 */
public class CompassView extends View
{
	/*
	 * Some constants defining appearance. Could be mutable but let's not
	 * complicate it.
	 */
	private static final float RADIUS_FACTOR = 0.2f;
	private static final int CIRCLE_COLOR = 0xFF8A8A8A;
	private static final float NEEDLE_WIDTH = 0.1f;
	private static final float NEEDLE_LENGTH = 0.7f;
	private static final int NEEDLE_NORTH_COLOR = 0xFFDB1237;
	private static final int NEEDLE_NORTH_SHADOW_COLOR = 0xFF990C26;
	private static final int NEEDLE_SOUTH_COLOR = 0xFFE3F4FC;
	private static final int NEEDLE_SOUTH_SHADOW_COLOR = 0xFF7E888C;
	private static final int TEXT_COLOR = 0xFF000000;
	private static final int DOT_RADIUS = 10;
	private static final float TEXT_SIZE = 20.0f;
	
	/*
	 * Coordinate position and orientation.
	 */
	private float radius = 1;
	private Float2 center = new Float2(0, 0);
	private Float2 north = new Float2(0, -1);
	private Float2 east = new Float2(1, 0);
	private Float3 worldPosition = new Float3(1, 0, 0);
	private Float3 worldNorth = new Float3(0, 0, 1);
	private Float3 worldEast = new Float3(0, 1, 0);
	
	/*
	 * Primitives to be drawn.
	 */
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Path path = new Path();
	private RectF border = new RectF();
	private RectF dotRect = new RectF();
	private List<Place> places = new LinkedList<Place>();
	private List<Float2> dots = new LinkedList<Float2>();
	private Float2 offset = new Float2();
	
	public CompassView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		paint.setTextSize(TEXT_SIZE);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		// Draw border oval.
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(CIRCLE_COLOR);
		canvas.drawOval(border, paint);
		
		//Draw needle
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(NEEDLE_NORTH_COLOR);
		path.reset();
		path.moveTo(center.x, center.y);
		path.lineTo(
				center.x + NEEDLE_WIDTH * east.x,
				center.y + NEEDLE_WIDTH * east.y
			);
		path.lineTo(
				center.x + NEEDLE_LENGTH * north.x,
				center.y + NEEDLE_LENGTH * north.y
			);
		path.close();
		canvas.drawPath(path, paint);
		
		paint.setColor(NEEDLE_NORTH_SHADOW_COLOR);
		path.reset();
		path.moveTo(center.x, center.y);
		path.lineTo(
				center.x - NEEDLE_WIDTH * east.x,
				center.y - NEEDLE_WIDTH * east.y
			);
		path.lineTo(
				center.x + NEEDLE_LENGTH * north.x,
				center.y + NEEDLE_LENGTH * north.y);
		path.close();
		canvas.drawPath(path, paint);
		
		paint.setColor(NEEDLE_SOUTH_COLOR);
		path.reset();
		path.moveTo(center.x, center.y);
		path.lineTo(
				center.x - NEEDLE_WIDTH * east.x,
				center.y - NEEDLE_WIDTH * east.y
			);
		path.lineTo(
				center.x - NEEDLE_LENGTH * north.x,
				center.y - NEEDLE_LENGTH * north.y
			);
		path.close();
		canvas.drawPath(path, paint);
		
		paint.setColor(NEEDLE_SOUTH_SHADOW_COLOR);
		path.reset();
		path.moveTo(center.x, center.y);
		path.lineTo(
				center.x + NEEDLE_WIDTH * east.x,
				center.y + NEEDLE_WIDTH * east.y
			);
		path.lineTo(
				center.x - NEEDLE_LENGTH * north.x,
				center.y - NEEDLE_LENGTH * north.y
			);
		path.close();
		canvas.drawPath(path, paint);
		
		// Draw places.
		Iterator<Float2> dotIt = dots.iterator();
		Iterator<Place> placeIt = places.iterator();
		while (dotIt.hasNext())
		{
			// Draw colored dot.
			Float2 dot = dotIt.next();
			Place place = placeIt.next();
			dotRect.set(
					center.x - DOT_RADIUS,
					center.y - DOT_RADIUS,
					center.x + DOT_RADIUS,
					center.y + DOT_RADIUS
				);
			offset.set(
					dot.x * east.x + dot.y * north.x,
					dot.x * east.y + dot.y * north.y
				);
			dotRect.offset(offset.x, offset.y);
			paint.setColor(place.getColor());
			canvas.drawOval(dotRect, paint);
			
			// Draw name
			paint.setColor(TEXT_COLOR);
			path.reset();
			path.moveTo(center.x + offset.x, center.y + offset.y);
			path.lineTo(center.x + 10 * offset.x,
					center.y + 10 * offset.y); // Hack. Fix it.
			canvas.drawTextOnPath(place.getName(), path, DOT_RADIUS + 2,
					paint.getTextSize() * 0.3f, paint);
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		float oldRadius = Math.min(oldw, oldh) * RADIUS_FACTOR;
		if (oldRadius == 0)
			oldRadius = 1;
		radius = Math.min(w, h) * RADIUS_FACTOR;
		center.set(w / 2, h / 2);
		east.mul(radius / oldRadius);
		north.mul(radius / oldRadius);
		border.set(-radius, -radius, radius, radius);
		border.offset(center.x, center.y);
		invalidate();
	}
	
	/*
	 * Set the coordinates of the observer.
	 */
	public void setCoordinates(float latitude, float longitude)
	{
		worldPosition = Coordinates.positionOnSphere(latitude, longitude);
		// Extra caution near poles.
		if (Math.abs(latitude) < Coordinates.CRITICAL_LATITUDE)
			worldEast.set(
					FloatMath.sin(Coordinates.degToRad(longitude)),
					FloatMath.cos(Coordinates.degToRad(longitude)),
					0
				);
		else if (latitude <= 0)
		{
			Float3 toNorthPole = Float3.dif(new Float3(0, 0, 1), worldPosition);
			worldEast = Float3.cross(toNorthPole, worldPosition).normalized();
		}
		else
		{
			Float3 toSouthPole = Float3.dif(new Float3(0, 0,-1), worldPosition);
			worldEast = Float3.cross(worldPosition, toSouthPole).normalized();
		}
		worldNorth = Float3.cross(worldPosition, worldEast);
		
		// Recompute directions to places.
		Iterator<Float2> dotIt = dots.iterator();
		Iterator<Place> placeIt = places.iterator();
		while (dotIt.hasNext())
			dotIt.next().set(computeDot(placeIt.next()));
	}
	
	public void addPlaces(List<Place> places)
	{
		this.places.addAll(places);
		for (Place place : places)
			dots.add(computeDot(place));
	}
	
	public void addPlace(Place place)
	{
		places.add(place);
		dots.add(computeDot(place));
	}
	
	/*
	 * Computes local coordinate system given the rotation matrix that
	 * transforms the phone coordinates into world coordinates.
	 * 
	 * See SensorManager.getRotationMarix().
	 */
	public void setRotationMatrix(float[] rot)
	{
		// Assign projected world directions to be local directions
		east.set(rot[0], rot[1]);
		north.set(rot[3], rot[4]);

		float orientation = 1;
		// If the phone is held screen to ground, flip handedness of the
		// coordinate system.
		if (-rot[8] > Math.abs(rot[6]) && -rot[8] > Math.abs(rot[7]))
			orientation = -1;
		
		// Choose the longer projection and determine the other one with respect
		// to it.
		if (east.lengthSq() > north.lengthSq())
		{
			east.normalize();
			north.set(-east.y, east.x);
			north.mul(orientation);
		}
		else
		{
			north.normalize();
			east.set(north.y, -north.x);
			east.mul(orientation);
		}
		
		// Flip the y-component since the canvas coordinate system is y-down.
		east.y = -east.y;
		north.y = -north.y;
		
		// Adjust the length of the unit vectors to be radius.
		east.mul(radius);
		north.mul(radius);
		         
		invalidate();
	}
	
	/*
	 * Compute position of the colored dot associated with the place.
	 * It determines the direction in which the place is drawn.
	 */
	private Float2 computeDot(Place place)
	{
		Float3 placePosition = Coordinates.positionOnSphere(
				place.getLatitude(), place.getLongitude());
		Float3 toPlace = Float3.dif(placePosition, worldPosition);
		Float2 toPlaceProjected = new Float2(
				Float3.dot(worldEast, toPlace),
				Float3.dot(worldNorth, toPlace)
			);
		float len = toPlaceProjected.length();
		if (len == 0)
			toPlaceProjected.set(1, 0);
		else
			toPlaceProjected.div(len);
		return toPlaceProjected;
	}
}

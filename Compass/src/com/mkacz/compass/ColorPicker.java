package com.mkacz.compass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

/*
 * A simple color picker view that can be embedded in a layout. Lets the user
 * choose from predefined swatches. Has very limited functionality, certainly
 * not general purpose.
 */
public class ColorPicker extends View
{
	/*
	 * The color array. Could as well be modal, but let's not complicate it
	 * at this point.
	 */
	private final int[] swatches = new int[] {
		0xFF33B5E5,
		0xFFAA66CC,
		0xFF99CC00,
		0xFFFFBB33,
		0xFFFF4444
	};

	/*
	 * Swatch rects in view coordinates and a Paint object. Both used for
	 * drawing.
	 */
	private Rect[] rects;
	private RectF ovalRect = new RectF();
	private int ovalMargin = 0;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	private int picked = 0;
	
	public ColorPicker(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		rects = new Rect[swatches.length];
		for (int s = 0; s < swatches.length; ++s)
			rects[s] = new Rect();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		recomputeRects(w, h);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		for (int s = 0; s < swatches.length; ++s)
		{
			paint.setColor(swatches[s]);
			canvas.drawRect(rects[s], paint);
		}
		
		paint.setColor(Color.WHITE); // Wont be visible on white background but
		                             // who cares right now.
		Rect pickedRect = rects[picked];
		ovalRect.set(
				pickedRect.left + ovalMargin,
				pickedRect.top + ovalMargin,
				pickedRect.right - ovalMargin,
				pickedRect.bottom - ovalMargin
			);
		canvas.drawOval(ovalRect, paint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			int x = (int) event.getX();
			int y = (int) event.getY();
			for (int s = 0; s < swatches.length; ++s)
				if (rects[s].contains(x, y))
				{
					picked = s;
					invalidate();
					break;
				}
		}
		return true;
	}
	
	public int getPickedColor()
	{
		return swatches[picked];
	}
	
	/*
	 * Sets the picked color. If given color is not in the swatches array, then
	 * the first color is set as picked.
	 */
	public void setPickedColor(int color)
	{
		picked = 0;
		for (int s = 0; s < swatches.length; ++s)
			if (swatches[s] == color)
			{
				picked = s;
				break;
			}
	}
	
	/*
	 * Recompute swatch rectangles to best fit the available area.
	 */
	private void recomputeRects(int w, int h)
	{
		int size = (int) FloatMath.sqrt((float) w * h / swatches.length);
		int columnCnt = (int) FloatMath.ceil((float) w / size);
		int rowCnt = (int) FloatMath.ceil((float) h / size);
		if (rowCnt == 1)
			columnCnt = swatches.length;
		size = Math.min(w / columnCnt, h / rowCnt);
		int margin = size / 20;
		int left = (w - columnCnt * size) / 2;
		int top = (h - rowCnt * size) / 2;
		ovalMargin = size / 4;
		
		for (int c = 0; c < swatches.length; ++c)
		{
			int i = c / columnCnt;
			int j = c - i * columnCnt;
			rects[c].set(
					left + j * size + margin,
					top + i * size + margin,
					left + (j + 1) * size - margin,
					top + (i + 1) * size - margin
				);
		}
	}
}
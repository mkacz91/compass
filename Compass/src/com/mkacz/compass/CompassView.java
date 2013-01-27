package com.mkacz.compass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View
{
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private float[] north = new float[] {0, -1};
	private float[] east = new float[] {1, 0};
	private float[] up = new float[] {0, 0};
	private float[] center = new float[2];
	
	public CompassView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		paint.setColor(Color.RED);
		canvas.drawLine(
				center[0],
				center[1],
				center[0] + 80 * east[0],
				center[1] + 80 * east[1],
				paint
			);
		
		paint.setColor(Color.GREEN);
		canvas.drawLine(
				center[0],
				center[1],
				center[0] + 80 * north[0],
				center[1] + 80 * north[1],
				paint
			);
		
		paint.setColor(Color.BLUE);
		canvas.drawLine(
				center[0],
				center[1],
				center[0] + 80 * up[0],
				center[1] + 80 * up[1],
				paint
			);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		center[0] = w / 2;
		center[1] = h / 2;
	}
	
	void setRotationMatrix(float[] rotation)
	{
		assert (rotation != null && rotation.length == 9);

		float[] e = new float[] {rotation[0], rotation[1], rotation[2]};
		float[] n = new float[] {rotation[3], rotation[4], rotation[5]};
		float[] u = new float[] {rotation[6], rotation[7], rotation[8]};
		
		east[0] = e[0];
		east[1] = -e[1];
		north[0] = n[0];
		north[1] = -n[1];
		up[0] = u[0];
		up[1] = -u[1];
		
		/*
		
		if (Math.abs(n[1]) > Math.abs(n[2]))
		{
			float l = FloatMath.sqrt(n[0] * n[0] + n[1] * n[1]);
			north[0] = n[0] / l;
			north[1] = n[1] / l;
		}
		else
		{
			float l = FloatMath.sqrt(n[0] * n[0] + n[2] * n[2]);
			north[0] = -n[2] / l;
			north[1] = n[1] / l;
		}
		*/
		invalidate();
	}
}

package com.mkacz.vector_math;

import android.util.FloatMath;

public class Float2
{
	public float x;
	public float y;
	
	public Float2()
	{
		x = 0;
		y = 0;
	}
	
	public Float2(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void set(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void mul(float s)
	{
		x *= s;
		y *= s;
	}
	
	public void div(float s)
	{
		x /= s;
		y /= s;
	}
	
	public float lengthSq()
	{
		return x * x + y * y;
	}
	
	public float length()
	{
		return FloatMath.sqrt(lengthSq());
	}
	
	public void normalize()
	{
		div(length());
	}
}

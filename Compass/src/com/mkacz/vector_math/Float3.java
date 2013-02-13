package com.mkacz.vector_math;

import android.util.FloatMath;

public class Float3
{
	public float x;
	public float y;
	public float z;
	
	public Float3()
	{
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Float3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Float3 add(Float3 a, Float3 b)
	{
		return new Float3(a.x + b.x, a.y + b.y, a.z + b.z);
	}
	
	public static Float3 dif(Float3 a, Float3 b)
	{
		return new Float3(a.x - b.x, a.y - b.y, a.z - b.z);
	}
	
	public static Float3 mul(Float3 a, float s)
	{
		return new Float3(a.x * s, a.y * s, a.z * s);
	}
	
	public static Float3 div(Float3 a, float s)
	{
		return new Float3(a.x / s, a.y / s, a.z / s);
	}
	
	public void div(float s)
	{
		x /= s;
		y /= s;
		z /= s;
	}
	
	public static Float3 cross(Float3 a, Float3 b)
	{
		return new Float3(
				a.y * b.z - a.z * b.y,
				a.z * b.x - a.x * b.z,
				a.x * b.y - a.y * b.x
			);
	}
	
	public static float dot(Float3 a, Float3 b)
	{
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}
	
	public float lengthSq()
	{
		return x * x + y * y + z * z;
	}
	
	public float length()
	{
		return FloatMath.sqrt(lengthSq());
	}
	
	public void normalize()
	{
		div(length());
	}
	
	public Float3 normalized()
	{
		return div(this, length());
	}
}
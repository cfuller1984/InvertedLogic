package com.invertedlogic.util;

import com.badlogic.gdx.math.Vector2;

public class MathHelper {
	public static float clamp(float pValue, final float pMin, final float pMax) {
		if (pValue < pMin) pValue = pMin;
		if (pValue > pMax) pValue = pMax;
		
		return pValue;
	}
	
	public static float lerp(final float pValue1, final float pValue2, final float pAmount) {
		return pValue1 + (pValue2 - pValue1) * pAmount;
	}
	
	public static int lerp(final int pValue1, final int pValue2, final float pAmount) {
		return (int)(pValue1 + (pValue2 - pValue1) * pAmount);
	}
	
	public static Vector2 lerp(final Vector2 pValue1, final Vector2 pValue2, final float pAmount) {
		Vector2 dist = pValue2.cpy();
		dist.sub(pValue1);
		
		return pValue1.cpy().add(dist.mul(pAmount));
	}
	
	public static float easeIn(float pRatio)
	{
		return 1.0f-(1.0f-pRatio)*(1.0f-pRatio);
	}
	
	public static float easeOut(float pRatio)
	{
		return pRatio*pRatio;
	}
	
	public static float smoothStep(float pMin, float pMax, float pRatio)
	{
		float term = ( ( pRatio - pMin ) / ( pMax - pMin ) );
		float termSq = term * term;

		return ( ( -2.0f * termSq * term ) + ( 3.0f * termSq ) );
	}
	
	public static float catmullrom(float t, float p0, float p1, float p2, float p3)
	{
		return 0.5f * (
		              (2 * p1) +
		              (-p0 + p2) * t +
		              (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t +
		              (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t
		              );
	}
	
	public static int roundToPowerOfTwo(int pValue) {
		--pValue;
		pValue |= pValue >> 1;
		pValue |= pValue >> 2;
		pValue |= pValue >> 4;
		pValue |= pValue >> 8;
		pValue |= pValue >> 16;
		++pValue;
		return pValue;
	}
}

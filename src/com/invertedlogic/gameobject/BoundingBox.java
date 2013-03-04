package com.invertedlogic.gameobject;


public class BoundingBox {
	float mMinX;
	float mMinY;
	float mMaxX;
	float mMaxY;
	
	public BoundingBox() {
		reset();
	}
	
	public BoundingBox(float pMinX, float pMinY, float pMaxX, float pMaxY) {
		mMinX = pMinX;
		mMinY = pMinY;
		mMaxX = pMaxX;
		mMaxY = pMaxY;
	}
	
	public void reset() {
		mMinX = Float.MAX_VALUE;
		mMinY = Float.MAX_VALUE;
		mMaxX = Float.MIN_VALUE;
		mMaxY = Float.MIN_VALUE;
	}
	
	public void expand(BoundingBox pBoundingBox) {
		expand(pBoundingBox.mMinX, pBoundingBox.mMinY, pBoundingBox.mMaxX, pBoundingBox.mMaxY);
	}
	
	public void expand(float pMinX, float pMinY, float pMaxX, float pMaxY) {
		if (pMinX < mMinX) mMinX = pMinX;
		if (pMinY < mMinY) mMinY = pMinY;
		if (pMaxX > mMaxX) mMaxX = pMaxX;
		if (pMaxY > mMaxY) mMaxY = pMaxY;
	}
	
	public float getMinX() {
		return mMinX;
	}
	
	public float getMinY() {
		return mMinY;
	}
	
	public float getMaxX() {
		return mMaxX;
	}
	
	public float getMaxY() {
		return mMaxY;
	}
	
	public float getWidth() {
		return mMaxX - mMinX;
	}
	
	public float getHeight() {
		return mMaxY - mMinY;
	}
}

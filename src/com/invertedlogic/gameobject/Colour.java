package com.invertedlogic.gameobject;

import com.badlogic.gdx.graphics.Color;

public class Colour {
	float mRed;
	float mGreen;
	float mBlue;
	float mAlpha;
	
	Color mColor;
	
	public Colour() {
		mColor = new Color(Color.PINK);
	}
	
	public Colour(float pRed, float pGreen, float pBlue, float pAlpha) {
		mRed = pRed;
		mGreen = pGreen;
		mBlue = pBlue;
		mAlpha = pAlpha;
		
		mColor.set(pRed, pGreen, pBlue, pAlpha);
	}
	
	public boolean parseAttributeFromXml(String pName, String pValue) {
		boolean result = true;
		
		if (pName.equalsIgnoreCase("r")) {
			mRed = Float.parseFloat(pValue);
		} else if (pName.equalsIgnoreCase("g")) {
			mGreen = Float.parseFloat(pValue);
		} else if (pName.equalsIgnoreCase("b")) {
			mBlue = Float.parseFloat(pValue);
		} else if (pName.equalsIgnoreCase("a")
				|| pName.equalsIgnoreCase("alpha")) {
			mAlpha = Float.parseFloat(pValue);
		} else {
			result = false;
		}
		
		return result;
	}
	
	public void inheritFrom(Colour pColour) {
		mRed = pColour.mRed;
		mGreen = pColour.mGreen;
		mBlue = pColour.mBlue;
		mAlpha = pColour.mAlpha;

		mColor.set(mRed, mGreen, mBlue, mAlpha);
	}
	
	public Color getColor() {
		return mColor;
	}
	
	public float getRed() {
		return mRed;
	}
	
	public float getGreen() {
		return mGreen;
	}
	
	public float getBlue() {
		return mBlue;
	}
	
	public float getAlpha() {
		return mAlpha;
	}
}

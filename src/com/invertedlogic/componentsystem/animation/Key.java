package com.invertedlogic.componentsystem.animation;

import java.lang.reflect.Field;

import org.w3c.dom.Element;

import com.invertedlogic.componentsystem.ComponentFactory;
import com.invertedlogic.componentsystem.animation.AnimationClip.GameObjectReference;
import com.invertedlogic.gameobject.GameObject;
import com.invertedlogic.util.Util;

public class Key {
	protected Keyframe mKeyframe;
	protected GameObjectReference mGameObjectReference;
	
	protected String mId;
	protected Field mField;
	protected Object mObject;
	protected Object mValue;
	
	//protected ECurveType mLeftCurve;
	
	
	public Key(Keyframe pKeyframe) {
		mKeyframe = pKeyframe;
		mGameObjectReference = mKeyframe.mGameObjectReference;
	}
	
	@Override
	public boolean equals(Object pThat) {
		// If the two objects are equal
		if (this == pThat) return true;
		
		// If the object is not the correct class type
		if (!(pThat instanceof Key)) return false;
		
		Key key = (Key)pThat;
		return mId.equals(key.mId);
	}
	
	void parseFromXml(Element pXmlNode) {
		mId = pXmlNode.getAttribute("field");
		getFieldFromId(mId);
		
		if (mField.getType() == float.class) {
			mValue = Util.getFloatAttribute(pXmlNode, "value");
		} else if (mField.getType() == int.class) {
			mValue = Util.getIntAttribute(pXmlNode, "value", 0);
		}
	}
	
	void inheritFrom(Key pKey) {
		mId = pKey.mId;
		mValue = pKey.mValue;
		
		getFieldFromId(mId);
	}
	
	String getId() {
		return mId;
	}
	
	Field getField() {
		return mField;
	}
	
	Object getValue() {
		return mValue;
	}
	
	float getFloatValue() {
		return (Float)mValue;
	}
	
	void getFieldFromId(String pId) {
		String[] fieldIds = pId.split("\\.");
		
		Class<?> c = mGameObjectReference.mGameObject.getClass();
		
		mObject = mGameObjectReference.mGameObject;
		
		for (int i = 0; i < fieldIds.length; ++i) {
			String fieldId = fieldIds[i];
			
			try {
				mField = c.getDeclaredField(fieldId);
				
				if (i < fieldIds.length - 1) {
					try {
						mField.setAccessible(true);
						mObject = mField.get(mObject);
						mField.setAccessible(false);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				c = mField.getType();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				if (mObject.getClass() == GameObject.class) {
					GameObject go = (GameObject)mObject;
					c = ComponentFactory.GetComponentClass(fieldId);
					mObject = go.getComponentOfType(c);
				} else {
					e.printStackTrace();
				}
			}
		}

		mField.setAccessible(true);
	}
}

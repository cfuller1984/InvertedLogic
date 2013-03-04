package com.invertedlogic.componentsystem.animation;

import java.util.HashMap;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.invertedlogic.componentsystem.animation.AnimationClip.GameObjectReference;
import com.invertedlogic.util.Util;

public class Keyframe {
	Animation mAnimation;
	AnimationClip mAnimationClip;
	GameObjectReference mGameObjectReference;
	
	float mTime;
	HashMap<String, Key> mKeys = new HashMap<String, Key>();
	
	public Keyframe(GameObjectReference pGameObjectReference) {
		mGameObjectReference = pGameObjectReference;
		mAnimationClip = mGameObjectReference.mAnimationClip;
		mAnimation = mAnimationClip.mAnimation;
	}
	
	protected void parseFromXml(Element pXmlNode) {
		mTime = Util.getFloatAttribute(pXmlNode, "time");
		
		// Parse keys
		NodeList list = pXmlNode.getElementsByTagName("Key");
		
		//mKeys = new Key[list.getLength()];
		
		for (int i = 0; i < list.getLength(); i++) {
			Element keyNode = (Element)list.item(i);
			
			Key key = new Key(this);
			key.parseFromXml(keyNode);
			
			mKeys.put(key.getId(), key);
		}
	}
	
	protected void inheritFrom(Keyframe pKeyframe) {
		mTime = pKeyframe.mTime;
		
		for (Entry<String, Key> entry : pKeyframe.mKeys.entrySet()) {
			Key inheritKey = entry.getValue();
			Key key = new Key(this);
			key.inheritFrom(inheritKey);
			
			mKeys.put(key.getId(), key);
		}
	}
	
	protected float getTime() {
		return mTime;
	}
	
	protected boolean containsKey(String pKey) {
		return mKeys.containsKey(pKey);
	}
	
	protected Key getKey(String pKey) {
		return mKeys.get(pKey);
	}
}

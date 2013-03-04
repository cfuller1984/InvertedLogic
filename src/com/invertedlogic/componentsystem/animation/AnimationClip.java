package com.invertedlogic.componentsystem.animation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.badlogic.gdx.Gdx;
import com.invertedlogic.gameobject.GameObject;
import com.invertedlogic.gameobject.Transform;
import com.invertedlogic.util.EAnimState;
import com.invertedlogic.util.MathHelper;
import com.invertedlogic.util.Util;

public class AnimationClip {
	class GameObjectReference {
		AnimationClip mAnimationClip;
		String mGameObjectId;
		GameObject mGameObject;
		GameObjectReference mParent;
		GameObjectReference[] maChildren;
		
		Keyframe[] mKeyframes;
		
		public GameObjectReference(AnimationClip pAnimationClip, GameObject pGameObject) {
			mParent = null;
			mAnimationClip = pAnimationClip;
			mGameObject = pGameObject;
			mGameObjectId = pGameObject.getId();
		}
		
		public GameObjectReference(GameObjectReference pParent, AnimationClip pAnimationClip, String pGameObjectId) {
			mParent = pParent;
			mAnimationClip = pAnimationClip;
			mGameObjectId = pGameObjectId;
			mGameObject = mParent.mGameObject.findChild(mGameObjectId);
		}
		
		protected void parseFromXml(Element pXmlNode) {
			// Parse game objects
			ArrayList<Element> elements = new ArrayList<Element>();
			
			NodeList list = pXmlNode.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				if (list.item(i).getNodeType() == Node.ELEMENT_NODE
						&& list.item(i).getNodeName().equalsIgnoreCase("GameObject")) {
					elements.add((Element)list.item(i));
				}
			}
			
			if (!elements.isEmpty()) {
				maChildren = new GameObjectReference[elements.size()];
				
				for (int i = 0; i < elements.size(); ++i) {
					maChildren[i] = parseGameObjectFromXml(elements.get(i), this);
				}
			}
			
			parseKeyframesFromXml(pXmlNode);
		}
		
		protected void parseKeyframesFromXml(Element pXmlNode) {
			// Parse keyframes
			ArrayList<Element> elements = Util.getElementsByTagName(pXmlNode, "Keyframe");
			mKeyframes = new Keyframe[elements.size()];
			
			for (int i = 0; i < elements.size(); i++) {
				Element keyframeNode = elements.get(i);
				
				mKeyframes[i] = new Keyframe(this);
				mKeyframes[i].parseFromXml(keyframeNode);
				
				if (mKeyframes[i].getTime() > mDuration) {
					mDuration = mKeyframes[i].getTime();
				}
			}
		}
		
		protected GameObjectReference parseGameObjectFromXml(Element pXmlNode, GameObjectReference pParent) {
			String id = pXmlNode.getAttribute("id");
			
			//GameObject gameObject = pParent.mGameObject.findChild(id);
			
			GameObjectReference gameObjectReference = new GameObjectReference(this, mAnimationClip, id);
			gameObjectReference.parseFromXml(pXmlNode);
			
			return gameObjectReference;
		}
		
		protected void inheritFrom(GameObjectReference pGameObjectReference) {
			if (pGameObjectReference.maChildren != null) {
				maChildren = new GameObjectReference[pGameObjectReference.maChildren.length];
				
				for (int i = 0; i < pGameObjectReference.maChildren.length; ++i) {
					GameObjectReference inheritGameObjectRef = pGameObjectReference.maChildren[i];
					
					//GameObject gameObject = mGameObject.findChild(inheritGameObjectRef.mGameObject.getId());
					maChildren[i] = new GameObjectReference(this, mAnimationClip, inheritGameObjectRef.getGameObjectId());
					maChildren[i].inheritFrom(inheritGameObjectRef);
				}
			}
			
			mKeyframes = new Keyframe[pGameObjectReference.mKeyframes.length];
			for (int i = 0; i < pGameObjectReference.mKeyframes.length; ++i) {
				mKeyframes[i] = new Keyframe(this);
				mKeyframes[i].inheritFrom(pGameObjectReference.mKeyframes[i]);
			}
		}
		
		protected void setup() {
			if (mParent != null) {
				mGameObject = mParent.mGameObject.findChild(mGameObjectId);
			}
			
			if (maChildren != null) {
				for (GameObjectReference gameObjectRef : maChildren) {
					gameObjectRef.setup();
				}
			}
		}
		
		protected String getGameObjectId() {
			return mGameObjectId;
		}
		
		protected void onStatePlaying() {
			if (maChildren != null) {
				for (GameObjectReference gameObjectRef : maChildren) {
					gameObjectRef.onStatePlaying();
				}
			}
			
			for (int i = 0; i < mKeyframes.length - 1; ++i) {
				Keyframe keyframe = mKeyframes[i];
				
				// If this keyframe is potentially in range of the animation time
				if (mTime >= keyframe.getTime()) {
					onProcessKeyframe(i);
				}
			}
			
			mGameObject.onTransformChanged(true);
			mGameObject.onMaterialChanged(true);
		}
		
		protected void onProcessKeyframe(int pIndex) {
			Keyframe initialKeyframe = mKeyframes[pIndex];
			
			// For every key in this keyframe, find the next keyframe
			// that contains the same key and process it if the animation
			// time is in range
			Iterator<Entry<String, Key>> it = initialKeyframe.mKeys.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String, Key> pairs = it.next();
		        Key key = pairs.getValue();
		        
				boolean isLastKey = true;
				
				for (int i = pIndex + 1; i < mKeyframes.length; ++i) {
					Keyframe targetKeyframe = mKeyframes[i];
					if (mTime < targetKeyframe.getTime()) {
						float duration = targetKeyframe.mTime - initialKeyframe.mTime;
						float time = ((mTime - (/*mAnimation.mStartTime + */initialKeyframe.mTime)) / duration);
						/*
						switch (pStart.mCurveType) {
						case Linear:
							break;
						case EaseIn:
							time = MathHelper.easeIn(time);
							break;
						case EaseOut:
							time = MathHelper.easeOut(time);
							break;
						case SmoothStep:
							time = MathHelper.smoothStep(0.0f, 1.0f, time);
							break;
						}*/
						
						time = MathHelper.smoothStep(0.0f, 1.0f, time);
						
						if (targetKeyframe.containsKey(pairs.getKey())) {
							Key targetKey = targetKeyframe.getKey(pairs.getKey());
							
							float initialValue = 0.0f;
							float targetValue = 0.0f;
							
							if (key.getValue().getClass() == Float.class) {
								initialValue = (Float)key.getValue();
								targetValue = (Float)targetKey.getValue();
							} else if (key.getValue().getClass() == Integer.class) {
								initialValue = (Integer)key.getValue();
								targetValue = (Integer)targetKey.getValue();
							}
							
							float value = MathHelper.lerp(initialValue, targetValue, time);
							Field field = key.mField;
							
							try {
								if (field.getType() == float.class) {
									field.setFloat(key.mObject, value);
								} else if (field.getType() == int.class) {
									field.setInt(key.mObject, Math.round(value));
								}
								//mAnimation.getTransform().setTransformDirty(true);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
							
							isLastKey = false;
							break;
						}
					} else {
						if (targetKeyframe.containsKey(pairs.getKey())) {
							isLastKey = false;
							break;
						}
					}
				}
				
				if (isLastKey) {
					Field field = key.mField;
					
					try {
						field.setFloat(key.mObject, key.getFloatValue());
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	Animation mAnimation;
	Transform mTransform;
	GameObjectReference mGameObjectReference;
	
	//Keyframe[] mKeyframes;

	float mTime;
	float mDuration;
	EAnimState mState;
	
	boolean mLoop;
	
	public AnimationClip(Animation pAnimation) {
		mAnimation = pAnimation;
		mTransform = mAnimation.getTransform();
		mGameObjectReference = new GameObjectReference(this, mTransform.getGameObject());
		
		reset();
	}
	
	protected void parseFromXml(Element pXmlNode) {
		// Attributes
		NamedNodeMap attributes = pXmlNode.getAttributes();
		
		for (int i = 0; i < attributes.getLength(); ++i) {
			Node attribute = attributes.item(i);
			
			if (attribute.getNodeName().equalsIgnoreCase("loop")) {
				mLoop = Boolean.parseBoolean(attribute.getNodeValue());
			}
		}
		
		mGameObjectReference.parseFromXml(pXmlNode);
	}
	
	protected void inheritFrom(AnimationClip pAnimationClip) {
		mDuration = pAnimationClip.mDuration;
		mLoop = pAnimationClip.mLoop;
		
		mGameObjectReference.inheritFrom(pAnimationClip.mGameObjectReference);
	}
	
	protected void setup() {
		mGameObjectReference.setup();
	}
	
	protected void reset() {
		mTime = 0.0f;
		mState = EAnimState.Stopped;
	}
	
	protected void play() {
		mState = EAnimState.Playing;
		mTime = 0.0f;
	}
	
	protected boolean isPlaying() {
		return mState == EAnimState.Playing;
	}
	
	protected void update() {
		switch (mState) {
		case Playing:
			onStatePlaying();
			break;
		}
	}
	
	protected void onStatePlaying() {
		mTime += Gdx.graphics.getDeltaTime();

		if (mTime >= mDuration) {
			mTime = mDuration;
		}
		
		mGameObjectReference.onStatePlaying();
		
		if (Float.compare(mTime, mDuration) == 0) {
			if (mLoop) {
				play();
			} else {
				reset();
			}
		}
	}
}

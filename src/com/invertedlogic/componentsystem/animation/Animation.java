package com.invertedlogic.componentsystem.animation;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import com.invertedlogic.util.Assert;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.invertedlogic.componentsystem.Component;
import com.invertedlogic.gameobject.GameObject;

public class Animation extends Component {
	
	AnimationClip mCurrentAnimationClip;
	HashMap<String, AnimationClip> maAnimationClips;
	
	public Animation(GameObject pGameObject) {
		super(pGameObject, Component.skInstanceType_Single);
		
		maAnimationClips = new HashMap<String, AnimationClip>();
	}
	
	@Override
	public void onDestroy() {
		
	}
	
	@Override
	protected void parseFromXml(Element pXmlNode) {
		// Parse keyframes
		NodeList list = pXmlNode.getElementsByTagName("AnimationClip");
		for (int i = 0; i < list.getLength(); i++) {
			Element clipNode = (Element)list.item(i);
			
			String path = clipNode.getAttribute("path");
			
			File file = new File(path);  
			String id = file.getName();
			
			int index = id.lastIndexOf('.');
			if (index > 0
					&& index <= id.length() - 2) {
				id = id.substring(0, index);
			}  
			
			// Create the animation clip
			//AnimationClip animationClip = new AnimationClip(this);
				
			// Initialise the animation clip from Xml data
			//animationClip.parseFromXml(pXmlNode);
			
			AnimationClip clip = AnimationFactory.LoadAnimationClipFromXml(path, this);
			//clip.parseFromXml(clipNode);
			
			maAnimationClips.put(id, clip);
		}
	}

	@Override
	public void inheritFrom(Component pComponent) {
		super.inheritFrom(pComponent);
		
		Animation inheritAnimation = (Animation)pComponent;
		for (Entry<String, AnimationClip> entry : inheritAnimation.maAnimationClips.entrySet()) {
			AnimationClip inheritClip = entry.getValue();
			AnimationClip clip = new AnimationClip(this);
			clip.inheritFrom(inheritClip);
			
			maAnimationClips.put(entry.getKey(), clip);
		}
	}

	@Override
	public void setup() {
		for (Entry<String, AnimationClip> entry : maAnimationClips.entrySet()) {
			AnimationClip clip = entry.getValue();
			clip.setup();
		}
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		if (mCurrentAnimationClip != null) {
			mCurrentAnimationClip.update();
		}
	}
	
	public void play(boolean pInstant) {
		mCurrentAnimationClip.play();
	}
	
	public void play(String pId, boolean pInstant) {
		Assert.assertTrue(maAnimationClips.containsKey(pId));
		
		mCurrentAnimationClip = maAnimationClips.get(pId);
		play(pInstant);
	}
	
	public boolean isPlaying() {
		return mCurrentAnimationClip != null;
	}
	
	public boolean isPlaying(String pId) {
		if (maAnimationClips.containsKey(pId)
				&& mCurrentAnimationClip == maAnimationClips.get(pId)) {
			return mCurrentAnimationClip.isPlaying();
		} else {
			return false;
		}
	}
}

package com.invertedlogic.componentsystem.animation;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.invertedlogic.util.Assert;

public class AnimationFactory {
	static public Key CreateAnimationKey(Class<?> pClass) {
		Key key = null;
		Assert.assertNotNull(pClass);
		
		try {
			Constructor<?> ctor = pClass.getConstructor();
			key = (Key)ctor.newInstance();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return key;
	}
	
	static public AnimationClip LoadAnimationClipFromXml(String pFilename, Animation pAnimation) {
		AnimationClip animationClip = null;
		
		FileHandle fh = Gdx.files.internal(pFilename);
		
		if (fh.exists()) {
			Document document = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			InputStream is = fh.read();
			
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				document = db.parse(is);
			} catch (ParserConfigurationException e) {
			} catch (SAXException e) {
			} catch (IOException e) {
			}
			
			Element rootNode = document.getDocumentElement();
			animationClip = LoadAnimationClipInternal(rootNode, pAnimation);	
		}
		
		Assert.assertNotNull(animationClip);
		return animationClip;
	}
	
	static public AnimationClip LoadAnimationClipInternal(Element pXmlNode, Animation pAnimation) {
		AnimationClip animationClip = null;
		
		// Create the animation clip
		animationClip = new AnimationClip(pAnimation);
			
		// Initialise the animation clip from Xml data
		animationClip.parseFromXml(pXmlNode);
		
		return animationClip;
	}
}

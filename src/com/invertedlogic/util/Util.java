package com.invertedlogic.util;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Color;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;

public class Util {
	static FPSLogger smFPSLogger = new FPSLogger();
	public static FPSLogger GetFPSLogger() { return smFPSLogger; }
	
	public static void DebugLog(String pTag, String pMessage) {
		DebugLog(pTag, pMessage, true);
	}
	
	public static void DebugLog(String pTag, String pMessage, boolean pEnabled) {
		if (pEnabled) {
			Gdx.app.log(pTag, pMessage);
		}
	}
	
	public static int Color(float pR, float pG, float pB) {
		return Color.rgb((int)(pR*255.0f), (int)(pG*255.0f), (int)(pB*255.0f));
	}
	
	public static int getIntAttribute(Element pNode, String pAttr, int pDefault) {
		String value = pNode.getAttribute(pAttr);
		if (value != null
				&& value != "") {
			return Integer.valueOf(value).intValue();
		}
		
		return pDefault;
	}
	
	public static short getShortAttribute(Element pNode, String pAttr, short pDefault) {
		String value = pNode.getAttribute(pAttr);
		if (value != null
				&& value != "") {
			return Short.valueOf(value).shortValue();
		}
		
		return pDefault;
	}
	
	public static String getStringAttribute(Element pNode, String pAttr) {
		return pNode.getAttribute(pAttr);
	}
	
	public static String getStringAttribute(Element pNode, String pAttr, String pDefault) {
		String value = pNode.getAttribute(pAttr);
		return value != null ? value : pDefault;
	}
	
	public static float getFloatAttribute(Element pNode, String pAttr) {
		return getFloatAttribute(pNode, pAttr, 0f);
	}
	
	public static float getFloatAttribute(Element pNode, String pAttr, float pDefault) {
		String value = pNode.getAttribute(pAttr);
		if (value != null
				&& value != "") {
			if (value.endsWith("%")) {
				return Float.valueOf(value.substring(0, value.length() - 1)).floatValue() * 0.01f;
			} else {
				return Float.valueOf(value).floatValue();
			}
		}
		
		return pDefault;
	}
	
	public static EValueType getFloatAttributeValueType(Element pNode, String pAttr) {
		return getFloatAttributeValueType(pNode, pAttr, EValueType.Null);
	}
	
	public static EValueType getFloatAttributeValueType(Element pNode, String pAttr, EValueType pDefault) {
		String value = pNode.getAttribute(pAttr);
		if (value != null
				&& value != "") {
			if (value.endsWith("%")) {
				return EValueType.Percentage;
			} else {
				return EValueType.Absolute;
			}
		}
		
		return pDefault;
	}
	
	public static boolean getBooleanAttribute(Element pNode, String pAttr, boolean pDefault) {
		if (pNode.hasAttribute(pAttr)) {
			String value = pNode.getAttribute(pAttr);
			return Boolean.parseBoolean(value);
		} else {
			return pDefault;
		}
	}
	
	public static Boolean attributeExists(Element pNode, String pAttr) {
		return pNode.getAttribute(pAttr) != null;
	}
	
	public static Boolean isFloatPercentage(float pValue) {
		return pValue <-900.0f && pValue >-1100.0f;
	}
	
	public static ArrayList<Element> getElementsByTagName(Element pNode, String pTag) {
		ArrayList<Element> elements = new ArrayList<Element>();
		
		NodeList list = pNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				elements.add((Element)list.item(i));
			}
		}
		
		return elements;
	}
}

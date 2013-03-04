package com.invertedlogic.util;

import com.badlogic.gdx.Gdx;

public class Assert {
	public static void fail(String pMessage) {
		Gdx.app.error("assert", pMessage);
	}
	
	public static void assertTrue(boolean pCondition) {
		if (!pCondition) {
			Gdx.app.error("assert", "Assert");
		}
	}
	
	public static void assertTrue(String pMessage, boolean pCondition) {
		if (!pCondition) {
			Gdx.app.error("assert", pMessage);
		}
	}
	
	public static void assertFalse(boolean pCondition) {
		if (pCondition) {
			Gdx.app.error("assert", "Assert");
		}
	}
	
	public static void assertFalse(String pMessage, boolean pCondition) {
		if (pCondition) {
			Gdx.app.error("assert", pMessage);
		}
	}
	
	public static void assertNotNull(String pMessage, Object pObject) {
		if (pObject == null) {
			Gdx.app.error("assert", pMessage);
		}
	}
	
	public static void assertNotNull(Object pObject) {
		if (pObject == null) {
			Gdx.app.error("assert", "Assert");
		}
	}
	
	public static void assertNull(String pMessage, Object pObject) {
		if (pObject != null) {
			Gdx.app.error("assert", pMessage);
		}
	}
	
	public static void assertNull(Object pObject) {
		if (pObject != null) {
			Gdx.app.error("assert", "Assert");
		}
	}
	
	
}

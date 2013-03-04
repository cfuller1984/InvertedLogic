package com.invertedlogic.graphics;

import java.util.HashMap;

public class LayerManager {
	static LayerManager smThis;
	public static void Create() { smThis = new LayerManager(); }
	public static LayerManager Get() { return smThis; }
	
	HashMap<String, Integer> mLayers;
	
	public LayerManager() {
		mLayers = new HashMap<String, Integer>();
		
		// Create built-in layers
		createLayer("Default");
		createLayer("UI");
	}
	
	public void createLayer(String pLayerId) {
		mLayers.put(pLayerId, mLayers.size());
	}
	
	public int getLayerMask(String pLayerId) {
		if (pLayerId.equalsIgnoreCase("Everything")) {
			return 0;
		} else if (pLayerId.equalsIgnoreCase("Nothing")) {
			return -1;
		} else {
			return 1 << mLayers.get(pLayerId);
		}
	}
}

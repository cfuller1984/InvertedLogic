package com.invertedlogic.assets;

import java.util.HashMap;

import com.badlogic.gdx.assets.AssetManager;

public class AssetFactory {
	static AssetFactory smThis;
	public static void Create() { smThis = new AssetFactory(); }
	public static AssetFactory Get() { return smThis; }
	
	AssetManager mAssetManager;
	protected HashMap<String, IAsset> mCache = new HashMap<String, IAsset>();
	
	public AssetFactory() {
		mAssetManager = new AssetManager();
	}
	
	public void addAssetToCache(IAsset pAsset) {
		mCache.put(pAsset.getId(), pAsset);
	}
	
	public IAsset findAsset(String pId) {
		return mCache.get(pId);
	}
	
	public AssetManager getAssetManager() {
		return mAssetManager;
	}
}

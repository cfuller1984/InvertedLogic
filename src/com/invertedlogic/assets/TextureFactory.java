package com.invertedlogic.assets;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextureFactory extends AssetFactory {
	protected static TextureFactory smThis = null;
	
	SpriteBatch mSpriteBatch;
	
	public static void Create() {
		smThis = new TextureFactory();
	}
	
	public static TextureFactory Get() {
		return smThis;
	}
	
	public TextureFactory() {
		mSpriteBatch = new SpriteBatch();
	}
	
	public static TextureAsset requestTexture(String pFilename, TextureFilter pFilter) {
		TextureAsset asset = (TextureAsset)smThis.findAsset(pFilename);
		if (asset == null) {
			// Create the asset
			asset = new TextureAsset(pFilename, pFilter);
			
			// Add the asset to the cache
			smThis.addAssetToCache(asset);
			//Util.DebugLog("asset", "Loaded and referenced new texture: " + pFilename, smLoggingEnabled);
		} else {
			//Util.DebugLog("asset", "Referenced texture: " + pFilename + " (Ref Count: " + (asset.getRefCount() + 1) + ")", smLoggingEnabled);
		}
		
		asset.incRefCount();
		return asset;
	}
	
	public static TextureAsset requestTiledTexture(String pFilename, TextureFilter pFilter, int pColumns, int pRows) {
		TextureAsset asset = (TextureAsset)smThis.findAsset(pFilename);
		if (asset == null) {
			// Create the asset
			asset = new TextureAsset(pFilename, pColumns, pRows, pFilter);
			
			// Add the asset to the cache
			smThis.addAssetToCache(asset);
			//Util.DebugLog("asset", "Loaded and referenced new texture: " + pFilename, smLoggingEnabled);
		} else {
			//Util.DebugLog("asset", "Referenced texture: " + pFilename + " (Ref Count: " + (asset.getRefCount() + 1) + ")", smLoggingEnabled);
		}
		
		asset.incRefCount();
		return asset;
	}
	
	public boolean allTexturesLoaded() {
		for (HashMap.Entry<String, IAsset> entry : mCache.entrySet()) {
		    if (!entry.getValue().isLoaded()) {
		    	return false;
		    }
		}
		
		return true;
	}
	
	public SpriteBatch getSpriteBatch() {
		return mSpriteBatch;
	}
}

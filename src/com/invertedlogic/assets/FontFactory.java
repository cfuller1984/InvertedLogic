package com.invertedlogic.assets;


public class FontFactory extends AssetFactory {
	protected static FontFactory smThis = null;
	
	public static void Create() {
		smThis = new FontFactory();
	}
	
	public static FontAsset requestFont(String pFontName) {
		FontAsset asset = (FontAsset)smThis.findAsset(pFontName);
		if (asset == null) {
			// Create the asset
			asset = new FontAsset(pFontName);
			
			// Add the asset to the cache
			smThis.addAssetToCache(asset);
			//Util.DebugLog("asset", "Loaded and referenced new texture: " + pFilename, smLoggingEnabled);
		} else {
			//Util.DebugLog("asset", "Referenced texture: " + pFilename + " (Ref Count: " + (asset.getRefCount() + 1) + ")", smLoggingEnabled);
		}
		
		asset.incRefCount();
		return asset;
	}
}

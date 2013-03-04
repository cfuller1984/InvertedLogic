package com.invertedlogic.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class FontAsset extends Asset {
	Texture mTexture;
	BitmapFont mFont;
	
	public FontAsset(String pId) {
		super(pId);
		
		BitmapFontParameter param = new BitmapFontParameter();
		param.flip = true;
		
		AssetManager assetManager = AssetFactory.Get().getAssetManager();
		assetManager.load("Fonts/" + pId + ".fnt", BitmapFont.class, param);

		while (!assetManager.update()) {}
		mFont = assetManager.get("Fonts/" + pId + ".fnt", BitmapFont.class);
	}
	
	public BitmapFont getFont() {
		return mFont;
	}
}

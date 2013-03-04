package com.invertedlogic.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.invertedlogic.util.Assert;

public class TextureAsset extends Asset {
	Texture mTexture;
	TextureRegion[] mTextureRegion;
	
	public TextureAsset(String pId, TextureFilter pFilter) {
		super(pId);
		
		TextureParameter param = new TextureParameter();
		param.minFilter = pFilter;
		param.magFilter = pFilter;
		
		AssetManager assetManager = AssetFactory.Get().getAssetManager();
		assetManager.load(pId, Texture.class, param);
		
		while (!assetManager.update()) {}
		mTexture = assetManager.get(pId, Texture.class);
		
		mTextureRegion = new TextureRegion[1];
		mTextureRegion[0] = new TextureRegion(mTexture);
		mTextureRegion[0].flip(false, true);
	}
	
	public TextureAsset(String pId, int pColumns, int pRows, TextureFilter pFilter) {
		super(pId);
		
		TextureParameter param = new TextureParameter();
		param.minFilter = pFilter;
		param.magFilter = pFilter;
		
		AssetManager assetManager = AssetFactory.Get().getAssetManager();
		assetManager.load(pId, Texture.class, param);
		
		while (!assetManager.update()) {}
		mTexture = assetManager.get(pId, Texture.class);
		
		mTextureRegion = new TextureRegion[pColumns * pRows];
		
		TextureRegion[][] tmp = TextureRegion.split(mTexture, mTexture.getWidth() / pColumns, mTexture.getHeight() / pRows);
		int index = 0;
        for (int i = 0; i < pRows; i++) {
                for (int j = 0; j < pColumns; j++) {
                        mTextureRegion[index] = tmp[i][j];
                		mTextureRegion[index].flip(false, true);
                		index++;
                }
        }
	}
	
	public TextureRegion getTexture() {
		return mTextureRegion[0];
	}
	
	public TextureRegion getTexture(int pFrame) {
		Assert.assertTrue(pFrame < mTextureRegion.length);
		return mTextureRegion[pFrame];
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		mTexture.dispose();
		mTextureRegion = null;
	}
}

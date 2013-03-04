package com.invertedlogic.componentsystem;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.invertedlogic.assets.TextureAsset;
import com.invertedlogic.assets.TextureFactory;
import com.invertedlogic.gameobject.Colour;
import com.invertedlogic.gameobject.GameObject;
import com.invertedlogic.util.Assert;

public class QuadComponent extends Component {
	String mTextureId = "gfx/white.png";
	int mTextureWidth = 32;
	int mTextureHeight = 32;
	int mTextureFrameCols = 1;
	int mTextureFrameRows = 1;
	int mTextureFrameRate = 30;
	protected int mTextureFrameIndex = 0;
	int mTextureFrameCount = 0;
	
	Image mSprite;
	TextureAsset mTextureAsset;
	
	TextureRegionDrawable mDrawable;
	
	public QuadComponent(GameObject pGameObject) {
		super(pGameObject, Component.skInstanceType_Single);
	}
	
	@Override
	public void onDestroy() {
		mTextureAsset.decRefCount();
		mTextureAsset = null;
	}
	
	@Override
	protected boolean parseAttributeFromXml(String pName, String pValue) {
		if (pName.equalsIgnoreCase("texture")) {
			mTextureId = pValue;
			return true;
		} else if (pName.equalsIgnoreCase("texture-width")) {
			mTextureWidth = Integer.valueOf(pValue);
			return true;
		} else if (pName.equalsIgnoreCase("texture-height")) {
			mTextureHeight = Integer.valueOf(pValue);
			return true;
		} else if (pName.equalsIgnoreCase("texture-frame-cols")) {
			mTextureFrameCols = Integer.valueOf(pValue);
			return true;
		} else if (pName.equalsIgnoreCase("texture-frame-rows")) {
			mTextureFrameRows = Integer.valueOf(pValue);
			return true;
		} else if (pName.equalsIgnoreCase("texture-frame-rate")) {
			mTextureFrameRate = Integer.valueOf(pValue);
			return true;
		} else if (pName.equalsIgnoreCase("texture-frame-index")) {
			mTextureFrameIndex = Integer.valueOf(pValue);
			return true;
		} else {
			return super.parseAttributeFromXml(pName, pValue);
		}
	}

	@Override
	public void inheritFrom(Component pComponent) {
		super.inheritFrom(pComponent);
		
		QuadComponent quad = (QuadComponent)pComponent;
		
		mTextureId = quad.mTextureId;
		mTextureWidth = quad.mTextureWidth;
		mTextureHeight = quad.mTextureHeight;
		mTextureFrameCols = quad.mTextureFrameCols;
		mTextureFrameRows = quad.mTextureFrameRows;
		mTextureFrameRate = quad.mTextureFrameRate;
		mTextureFrameIndex = quad.mTextureFrameIndex;
		mTextureFrameCount = quad.mTextureFrameCount;
	}
	
	@Override
	public void setup() {
		Assert.assertNull(mTextureAsset);
		
		if (mTextureFrameCols == 1 && mTextureFrameRows == 1) {
			mTextureAsset = TextureFactory.requestTexture(mTextureId, TextureFilter.Linear);
			//mSprite = new Sprite(0.0f, 0.0f, mTransform.getSizeX(), mTransform.getSizeY(), mTextureAsset.getTexture(), EngineData.getEngine().getVertexBufferObjectManager());
		} else {
			mTextureFrameCount = mTextureFrameCols * mTextureFrameRows;
			Assert.assertTrue(mTextureFrameIndex < mTextureFrameCount);
			
			mTextureAsset = TextureFactory.requestTiledTexture(mTextureId, TextureFilter.Linear, mTextureFrameCols, mTextureFrameRows);
			//mSprite = new TiledSprite(0.0f, 0.0f, mTransform.getSizeX(), mTransform.getSizeY(), (TiledTextureRegion)mTextureAsset.getTexture(), EngineData.getEngine().getVertexBufferObjectManager());
			//TiledSprite tiledSprite = (TiledSprite)mSprite;
			//tiledSprite.setCurrentTileIndex(mTextureFrameIndex);
		}
		
		mDrawable = new TextureRegionDrawable(mTextureAsset.getTexture());
		
		mSprite = new Image(mDrawable);
		mSprite.setTouchable(Touchable.disabled);
		mTransform.addComponentActor(mSprite);
	}

	@Override
	public void reset() {
	}

	@Override
	public void update() {
		if (mTextureFrameCount > 0) {
			mDrawable.setRegion(mTextureAsset.getTexture(mTextureFrameIndex));
		}
	}
	
	@Override
	public void render() {
	}
	
	@Override
	public void onMaterialChanged() {
		super.onMaterialChanged();
		
		Material material = (Material)mGameObject.getComponentOfType(Material.class);
		Colour colour = material.getColour();
		//float alpha = mColour.mAlpha;
		
		// Multiply the alpha by all parent components
		//if (mTransform.mParent != null) {
			//Transform parent = mTransform.mParent;
			
			//while (parent != null) {
			//	alpha *= parent.mGameObject.getAlpha();
			//	parent = parent.mParent;
			//}
		//}
		
		//mTextureAsset.getTexture().set
		mSprite.setColor(colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getAlpha());
	}
	
	@Override
	public void onTransformChanged() {
		super.onTransformChanged();

		if (mSprite != null) {
			float fSizeX = mTransform.getSizeX();
			float fSizeY = mTransform.getSizeY();
			/*
			// Multiply the size by all parent components
			if (mTransform.mParent != null) {
				Transform parent = mTransform.mParent;
				
				while (parent != null) {
					fSizeX *= parent.getComponent().getTransform().getSizeX();
					fSizeY *= parent.getComponent().getTransform().getSizeY();
					parent = parent.mParent;
				}
			}
			*/
			
			mSprite.setSize(fSizeX, fSizeY);
		}
	}
	
	public boolean isTextureAssetLoaded() {
		if (mTextureAsset != null) {
			return mTextureAsset.isLoaded();
		}
		
		return true;
	}
	
	public Image getSprite() {
		return mSprite;
	}
	
	public void setTextureFrameIndex(int pIndex) {
		mTextureFrameIndex = pIndex;
		Assert.assertTrue(mTextureFrameIndex < mTextureFrameCount);
		
		//if (mSprite.getClass() == TiledSprite.class) {
		//	TiledSprite tiledSprite = (TiledSprite)mSprite;
		//	tiledSprite.setCurrentTileIndex(mTextureFrameIndex);
		//}
	}
	/*
	public void animate() {
		AnimatedSprite anim = (AnimatedSprite)mSprite;
		anim.animate(1000 / mTextureFrameRate, true);
	}
	
	public void stopAnimation() {
		AnimatedSprite anim = (AnimatedSprite)mSprite;
		
		anim.stopAnimation();
	}*/
}

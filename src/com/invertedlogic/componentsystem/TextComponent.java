package com.invertedlogic.componentsystem;

import org.w3c.dom.Element;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.invertedlogic.assets.FontAsset;
import com.invertedlogic.assets.FontFactory;
import com.invertedlogic.gameobject.Colour;
import com.invertedlogic.gameobject.GameObject;
import com.invertedlogic.util.Align;

public class TextComponent extends Component {
	public class TextComponentStyle {
		String font;
	}
	
	FontAsset mFontAsset;
	Label mLabel;
	
	String mFontId;
	String mText;
	
	public TextComponent(GameObject pGameObject) {
		super(pGameObject, Component.skInstanceType_Single);
		mText = "";
		mFontId = "Arial";
	}
	
	@Override
	public void onDestroy() {
		mFontAsset.decRefCount();
		mFontAsset = null;
		
		mTransform.removeActor(mLabel);
		mLabel = null;
	}
	
	@Override
	protected void parseFromXml(Element pXmlNode) {
		super.parseFromXml(pXmlNode);
		
		// Text
		mText = pXmlNode.getAttribute("text");
		
		// Font
		if (pXmlNode.hasAttribute("font")) {
			String font = pXmlNode.getAttribute("font");
			mFontAsset = FontFactory.requestFont(font);
		} else {
			mFontAsset = FontFactory.requestFont("arial");
		}
	}
	
	@Override
	protected boolean parseAttributeFromXml(String pName, String pValue) {
		if (pName.equalsIgnoreCase("text"))
		{
			mText = pValue;
			return true;
		}
		else if (pName.equalsIgnoreCase("font"))
		{
			mFontId = pValue;
			return true;
		}
		else
		{
			return super.parseAttributeFromXml(pName, pValue);
		}
	}

	@Override
	public void inheritFrom(Component pComponent) {
		super.inheritFrom(pComponent);
		
		TextComponent text = (TextComponent)pComponent;
		
		mFontId = text.mFontId;
		mText = text.mText;
	}
	
	@Override
	public void setup() {
		mFontAsset = FontFactory.requestFont(mFontId);
		
		mLabel = new Label(mText, new LabelStyle(mFontAsset.getFont(), Color.BLACK));
		mLabel.setTouchable(Touchable.disabled);
		mTransform.addComponentActor(mLabel);
		//mTextObject = new Text(0f, 0f, mFontAsset.getFont(), mText, EngineData.getEngine().getVertexBufferObjectManager());
		//mTextObject.setZIndex(0);
		//mTransform.attachEntity(mTextObject);
		
		onTransformChanged();
		onMaterialChanged();
	}

	@Override
	public void update() {
	}
	
	public void setText(String pText) {
		mText = pText;
		mLabel.setText(pText);
		/*
		if (mText.length() >  mTextObject.getText().length()) {
			mTransform.detachEntity(mTextObject);
			mTextObject = new Text(0f, 0f, mFontAsset.getFont(), mText, EngineData.getEngine().getVertexBufferObjectManager());
			mTransform.attachEntity(mTextObject);
		} else {
			mTextObject.setText(mText);
		}*/
		
		onTransformChanged();
		onMaterialChanged();
	}
	
	@Override
	public void onTransformChanged() {
		super.onTransformChanged();
		
		if (mLabel != null) {
			//float fSizeX = mTransform.getSizeX();
			//float fSizeY = mTransform.getSizeY();
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
			
			float x = 0.0f;
			float y = 0.0f;
			
			if (mTransform.getAlign() == Align.Left) {
			} else if (mTransform.getAlign() == Align.Center) {
				x -= mLabel.getWidth() * 0.5f;
			} else if (mTransform.getAlign() == Align.Right) {
				x -= mLabel.getWidth();
			}
			
			if (mTransform.getAlignV() == Align.Top) {
			} else if (mTransform.getAlignV() == Align.Middle) {
				y -= mLabel.getHeight() * 0.5f;
			} else if (mTransform.getAlignV() == Align.Bottom) {
				y -= mLabel.getHeight();
			}
			
			mLabel.setPosition(x, y);
			//mTextObject.setSize(fSizeX, fSizeY);
		}
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
		
		mLabel.setColor(colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getAlpha());
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}

package com.invertedlogic.componentsystem;

import com.badlogic.gdx.graphics.Texture;
import com.invertedlogic.gameobject.Colour;
import com.invertedlogic.gameobject.GameObject;

public class Material extends Component {

	Colour mColour = new Colour();
	Texture[] mTextureSlot;
	
	public Material(GameObject pGameObject) {
		super(pGameObject, Component.skInstanceType_Single);
	}
	
	@Override
	public void onDestroy() {
		
	}
	
	@Override
	protected boolean parseAttributeFromXml(String pName, String pValue) {
		return mColour.parseAttributeFromXml(pName, pValue);
	}

	@Override
	public void inheritFrom(Component pComponent) {
		super.inheritFrom(pComponent);
		
		Material material = (Material)pComponent;
		
		mColour.inheritFrom(material.getColour());
	}

	@Override
	public void setup() {
	}

	@Override
	public void reset() {
	}

	@Override
	public void update() {
	}
	
	public Colour getColour() {
		return mColour;
	}
	
	public float getAlpha() {
		return mColour.getAlpha();
	}
}

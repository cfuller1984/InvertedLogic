package com.invertedlogic.componentsystem;

import org.w3c.dom.Element;

import com.invertedlogic.gameobject.GameObject;


public class ComponentLibrary extends Component {

	String mId;
	
	public String getId() {
		return mId;
	}
	
	public ComponentLibrary(GameObject pGameObject) {
		super(pGameObject, Component.skInstanceType_Single);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onDestroy() {
		
	}

	@Override
	protected void parseFromXml(Element pXmlNode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inheritFrom(Component pComponent) {
		// TODO Auto-generated method stub
		
	}	

	@Override
	public void setup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}

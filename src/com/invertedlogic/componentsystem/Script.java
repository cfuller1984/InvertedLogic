package com.invertedlogic.componentsystem;

import org.w3c.dom.Element;

import com.invertedlogic.gameobject.GameObject;

public class Script extends Component {

	public Script(GameObject pGameObject) {
		super(pGameObject, Component.skInstanceType_Multiple);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onDestroy() {
		
	}

	@Override
	protected void parseFromXml(Element pXmlNode) {
		super.parseFromXml(pXmlNode);
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

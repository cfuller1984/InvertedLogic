package com.invertedlogic.componentsystem;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.invertedlogic.gameobject.GameObject;

public class TouchCollider extends Collider {
	class InputListener extends com.badlogic.gdx.scenes.scene2d.InputListener {
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			return true;
		}
	}
	
	InputListener mInputListener;
	
	public TouchCollider(GameObject pGameObject) {
		super(pGameObject);
	}

	@Override
	public void setup() {
		mInputListener = new InputListener();
		mTransform.getActor().addListener(mInputListener);
	}
	
	@Override
	public void onVisibilityChanged() {
	}
	
	@Override
	public void onEnabled() {
	}
	
	@Override
	public void onDisabled() {
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}
}

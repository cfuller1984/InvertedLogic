package com.invertedlogic.gameobject;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.invertedlogic.scene.Scene;

public class TransformGroup extends Group {
	Transform mTransform;
	GameObject mGameObject;
	
	InputListener mInputListener;
	
	public TransformGroup(Transform pTransform) {
		mTransform = pTransform;
		mGameObject = mTransform.getGameObject();
		
		mInputListener = new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (event.getListenerActor() instanceof TransformGroup) {
					TransformGroup group = (TransformGroup)event.getListenerActor();
					return group.touchDown(event, x, y, pointer, button);
				} else {
					return false;
				}
			}
			
			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				if (event.getListenerActor() instanceof TransformGroup) {
					TransformGroup group = (TransformGroup)event.getListenerActor();
					group.touchDragged(event, x, y, pointer);
				}
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (event.getListenerActor() instanceof TransformGroup) {
					TransformGroup group = (TransformGroup)event.getListenerActor();
					group.touchUp(event, x, y, pointer, button);
				}
			}
		};
		
		addCaptureListener(mInputListener);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		int layers = Scene.Camera.getLayersFilter();
		
		if (layers == 0
				|| mGameObject.isLayerMaskSet(layers)) {
			super.draw(batch, parentAlpha);
		}
	}
	
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		return mGameObject.touchDown(event, x, y, pointer, button);
	}
	
	public void touchDragged(InputEvent event, float x, float y, int pointer) {
		mGameObject.touchDragged(event, x, y, pointer);
	}
	
	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		mGameObject.touchUp(event, x, y, pointer, button);
	}
	
	public GameObject getGameObject() {
		return mGameObject;
	}
}

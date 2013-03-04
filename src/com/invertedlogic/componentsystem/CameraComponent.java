package com.invertedlogic.componentsystem;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.invertedlogic.gameobject.GameObject;
import com.invertedlogic.scene.Scene;

public class CameraComponent extends Component {

	Camera mCamera;
	
	boolean ortho;
	int viewportWidth;
	int viewportHeight;
	int mLayersFilter;
	
	public CameraComponent(GameObject pGameObject) {
		super(pGameObject, Component.skInstanceType_Single);
	}

	@Override
	public void setup() {
		if (ortho) {
			mCamera = new OrthographicCamera(viewportWidth, viewportHeight);
			OrthographicCamera orthoCamera = (OrthographicCamera)mCamera;
			orthoCamera.setToOrtho(true, viewportWidth, viewportHeight);
		} else {
			mCamera = new PerspectiveCamera(45.0f, viewportWidth, viewportHeight);
		}
	}

	@Override
	public void update() {
		mCamera.update();
	}

	@Override
	public void render() {
		Scene scene = Scene.GetCurrentScene();
		scene.draw(this);
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public void onTransformChanged() {
		//float ox = Gdx.graphics.getWidth() / 2;
		//float oy = Gdx.graphics.getHeight() / 2;
		
		mCamera.position.set(mTransform.getWorldX(), mTransform.getWorldY(), 0.0f);
	}
	
	//public void setPosition(Vector3 pPosition) {
		//mCamera.position.set((Gdx.graphics.getWidth() / 2) - pPosition.x, (Gdx.graphics.getHeight() / 2) - pPosition.y, 0.0f);
	//}
	
	public Vector3 getPosition() {
		return mCamera.position;
	}
	
	public float getViewportWidth() {
		return mCamera.viewportWidth;
	}
	
	public float getViewportHeight() {
		return mCamera.viewportHeight;
	}
	
	public Camera getCamera() {
		return mCamera;
	}
	
	public void setLayersFilter(int pFilter) {
		mLayersFilter |= pFilter;
	}
	
	public int getLayersFilter() {
		return mLayersFilter;
	}
}

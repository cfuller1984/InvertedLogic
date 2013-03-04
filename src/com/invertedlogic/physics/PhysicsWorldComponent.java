package com.invertedlogic.physics;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.invertedlogic.assets.TextureFactory;
import com.invertedlogic.componentsystem.CameraComponent;
import com.invertedlogic.componentsystem.Component;
import com.invertedlogic.gameobject.GameObject;
import com.invertedlogic.scene.Scene;

public class PhysicsWorldComponent extends Component implements PhysicsConstants {
	World mPhysicsWorld;
	Box2DDebugRenderer mDebugRenderer;
	
	CameraComponent mCamera;
	float gravity;
	
	public PhysicsWorldComponent(GameObject pGameObject) {
		super(pGameObject, Component.skInstanceType_Single);
		
		mPhysicsWorld = PhysicsManager.Get().getPhysicsWorld();
		mDebugRenderer = PhysicsManager.Get().getDebugRenderer();
		
		gravity = SensorManager.GRAVITY_EARTH;
	}
	
	@Override
	public void onDestroy() {
		mPhysicsWorld = null;
	}

	@Override
	public void setup() {
		mPhysicsWorld.setGravity(new Vector2(0.0f, gravity));
		mCamera = (CameraComponent)Scene.FindGameObject("Main Camera").getComponentOfType(CameraComponent.class);
	}

	@Override
	public void reset() {
	}

	@Override
	public void update() {
		mPhysicsWorld.step(1.0f / 60.0f, 8, 3);
	}
	
	@Override
	public void render() {
		TextureFactory.Get().getSpriteBatch().setProjectionMatrix(mCamera.getCamera().combined);
		TextureFactory.Get().getSpriteBatch().begin();
		mDebugRenderer.render(mPhysicsWorld, mCamera.getCamera().combined.scale(PIXEL_TO_METER_RATIO_DEFAULT, PIXEL_TO_METER_RATIO_DEFAULT, 1.0f));
		TextureFactory.Get().getSpriteBatch().end();
	}
}

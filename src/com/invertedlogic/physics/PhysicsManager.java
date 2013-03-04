package com.invertedlogic.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.invertedlogic.util.Assert;

public class PhysicsManager {
	static PhysicsManager smThis = null;
	static public void Create(int pStepsPerSecond, boolean pDebugRender) { Assert.assertNull(smThis); smThis = new PhysicsManager(pStepsPerSecond, pDebugRender); }
	static public PhysicsManager Get() { Assert.assertNotNull(smThis); return smThis; }
	
	World mPhysicsWorld;
	PhysicsContactListener mContactListener;
	Box2DDebugRenderer mDebugRenderer;
	
	public PhysicsManager(int pStepsPerSecond, boolean pDebugRender) {
		mPhysicsWorld = new World(new Vector2(0.0f, 0.0f), false);
		mContactListener = new PhysicsContactListener();
		mPhysicsWorld.setContactListener(mContactListener);
		
		if (pDebugRender) {
			mDebugRenderer = new Box2DDebugRenderer(true, true, true, true, true);
		}
	}
	
	public World getPhysicsWorld() {
		return mPhysicsWorld;
	}
	
	public void update() {
		//mPhysicsWorld.update();
	}
	
	public boolean isDebugRenderingEnabled() {
		return mDebugRenderer != null;
	}
	
	public Box2DDebugRenderer getDebugRenderer() {
		return mDebugRenderer;
	}
}

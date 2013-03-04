package com.invertedlogic.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class PhysicsContactListener implements ContactListener {

	public void beginContact(Contact contact) {
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		
		RigidBody rigidBodyA = (RigidBody)bodyA.getUserData();
		RigidBody rigidBodyB = (RigidBody)bodyB.getUserData();
		
		rigidBodyA.onBeginCollision(rigidBodyB, contact);
		rigidBodyB.onBeginCollision(rigidBodyA, contact);
	}

	public void endContact(Contact contact) {
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		
		RigidBody rigidBodyA = (RigidBody)bodyA.getUserData();
		RigidBody rigidBodyB = (RigidBody)bodyB.getUserData();
		
		rigidBodyA.onEndCollision(rigidBodyB, contact);
		rigidBodyB.onEndCollision(rigidBodyA, contact);
	}

	public void preSolve(Contact contact, Manifold oldManifold) {
		//WorldManifold worldManifold = contact.getWorldManifold();
		
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		
		RigidBody rigidBodyA = (RigidBody)bodyA.getUserData();
		RigidBody rigidBodyB = (RigidBody)bodyB.getUserData();
		
		rigidBodyA.onCollision(rigidBodyB, contact);
		rigidBodyB.onCollision(rigidBodyA, contact);
	}

	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
}

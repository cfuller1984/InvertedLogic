package com.invertedlogic.physics;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.invertedlogic.componentsystem.Component;
import com.invertedlogic.gameobject.GameObject;
import com.invertedlogic.util.Align;
import com.invertedlogic.util.Assert;
import com.invertedlogic.util.EPhysicsBodyShape;

public class RigidBody extends Component implements PhysicsConstants {
	class FixtureData {
		public float x;
		public float y;
		public float sizeX;
		public float sizeY;
		public float radius;
		public float density;
		public boolean isstatic;
		public boolean sensor;
		public String bodyShape;
		public String userData;
		
		public FixtureData(float pSizeX, float pSizeY) {
			sizeX = pSizeX;
			sizeY = pSizeY;
			density = 1.0f;
			isstatic = false;
			sensor = false;
			bodyShape = "box";
			userData = "";
		}
		
		public void inheritFrom(FixtureData pFixtureData) {
			x = pFixtureData.x;
			y = pFixtureData.y;
			if (pFixtureData.sizeX > 0) {
				sizeX = pFixtureData.sizeX;
			}
			
			if (pFixtureData.sizeY > 0) {
				sizeY = pFixtureData.sizeY;
			}
			density = pFixtureData.density;
			sensor = pFixtureData.sensor;
			userData = pFixtureData.userData;
			radius = pFixtureData.radius;
			bodyShape = pFixtureData.bodyShape;
			isstatic = pFixtureData.isstatic;
		}
	}
	
	Body mBody;
	FixtureData[] mFixtures;
	
	EPhysicsBodyShape mBodyShape = EPhysicsBodyShape.Box;
	BodyType mBodyType = BodyType.DynamicBody;
	
	World mWorld;
	
	float density = 1.0f;
	float mElasticity = 0.0f;
	float mFriction = 0.5f;
	boolean mSensor = false;
	boolean mFixedRotation = false;
	boolean ignoreGravity = false;
	
	short mCategoryBit = 0x0001;
	short mMaskBit =-1;
	short mGroupIndex = 0;
	
	public Body getBody() {
		return mBody;
		
	}
	public RigidBody(GameObject pGameObject) {
		super(pGameObject, Component.skInstanceType_Single);
	}
	
	@Override
	public void onDestroy() {
		PhysicsManager.Get().getPhysicsWorld().destroyBody(mBody);
		mBody = null;
	}
	
	@Override
	protected void parseFromXml(Element pXmlNode) {
		super.parseFromXml(pXmlNode);
		
		NodeList list = pXmlNode.getElementsByTagName("Fixture");
		
		if (list.getLength() > 0) {
			mFixtures = new FixtureData[list.getLength()];
			
			for (int i = 0; i < list.getLength(); i++) {
				Element fixtureNode = (Element)list.item(i);
	
				mFixtures[i] = new FixtureData(0, 0);
				parseFixtureFromXml(fixtureNode, i);
			}
		}
	}
	
	void parseFixtureFromXml(Element pXmlNode, int pIndex) {
		NamedNodeMap attributes = pXmlNode.getAttributes();
		
		for (int i = 0; i < attributes.getLength(); ++i) {
			Node attribute = attributes.item(i);
			
			if (attribute.getNodeName().equalsIgnoreCase("density")) {
				mFixtures[pIndex].density = Float.parseFloat(attribute.getNodeValue());
			} else if (attribute.getNodeName().equalsIgnoreCase("sensor")) {
				mFixtures[pIndex].sensor = Boolean.parseBoolean(attribute.getNodeValue());
			} else if (attribute.getNodeName().equalsIgnoreCase("static")) {
				mFixtures[pIndex].isstatic = Boolean.parseBoolean(attribute.getNodeValue());
			} else if (attribute.getNodeName().equalsIgnoreCase("x")) {
				mFixtures[pIndex].x = Float.parseFloat(attribute.getNodeValue());
			} else if (attribute.getNodeName().equalsIgnoreCase("y")) {
				mFixtures[pIndex].y = Float.parseFloat(attribute.getNodeValue());
			} else if (attribute.getNodeName().equalsIgnoreCase("size-x")) {
				mFixtures[pIndex].sizeX = Float.parseFloat(attribute.getNodeValue());
			} else if (attribute.getNodeName().equalsIgnoreCase("size-y")) {
				mFixtures[pIndex].sizeY = Float.parseFloat(attribute.getNodeValue());
			} else if (attribute.getNodeName().equalsIgnoreCase("user-data")) {
				mFixtures[pIndex].userData = attribute.getNodeValue();
			} else if (attribute.getNodeName().equalsIgnoreCase("body-shape")) {
				mFixtures[pIndex].bodyShape = attribute.getNodeValue();
			} else if (attribute.getNodeName().equalsIgnoreCase("radius")) {
				mFixtures[pIndex].radius = Float.parseFloat(attribute.getNodeValue());
			}
		}
	}
	
	@Override
	protected boolean parseAttributeFromXml(String pName, String pValue) {
		if (pName.equalsIgnoreCase("static")) {
			if (Boolean.parseBoolean(pValue)) {
				mBodyType = BodyType.StaticBody;
			}
			return true;
		} else if (pName.equalsIgnoreCase("kinematic")) {
			if (Boolean.parseBoolean(pValue)) {
				mBodyType = BodyType.KinematicBody;
			}
			return true;
		} else if (pName.equalsIgnoreCase("dynamic")) {
			if (Boolean.parseBoolean(pValue)) {
				mBodyType = BodyType.DynamicBody;
			}
			return true;
		} else if (pName.equalsIgnoreCase("fixed-rotation")) {
			mFixedRotation = Boolean.parseBoolean(pValue);
			return true;
		} else if (pName.equalsIgnoreCase("elasticity")) {
			mElasticity = Float.parseFloat(pValue);
			return true;
		} else if (pName.equalsIgnoreCase("friction")) {
			mFriction = Float.parseFloat(pValue);
			return true;
		} else if (pName.equalsIgnoreCase("sensor")) {
			mSensor = Boolean.parseBoolean(pValue);
			return true;
		} else {
			return super.parseAttributeFromXml(pName, pValue);
		}
	}

	@Override
	public void inheritFrom(Component pComponent) {
		super.inheritFrom(pComponent);
		
		RigidBody rigidBody = (RigidBody)pComponent;
		
		mBodyShape = rigidBody.mBodyShape;
		mBodyType = rigidBody.mBodyType;
		
		density = rigidBody.density;
		mElasticity = rigidBody.mElasticity;
		mFriction = rigidBody.mFriction;
		
		mSensor = rigidBody.mSensor;
		mFixedRotation = rigidBody.mFixedRotation;
		
		ignoreGravity = rigidBody.ignoreGravity;
		
		if (rigidBody.mFixtures != null) {
			mFixtures = new FixtureData[rigidBody.mFixtures.length];
			for (int i = 0; i < rigidBody.mFixtures.length; ++i) {
				mFixtures[i] = new FixtureData(0, 0);
				mFixtures[i].inheritFrom(rigidBody.mFixtures[i]);
			}
		}
	}
	
	@Override
	public void setup() {
		Assert.assertNull(mBody);
		
		mWorld = PhysicsManager.Get().getPhysicsWorld();
		
		// Create the physics body
		//final FixtureDef fixtureDef = new FixtureDef();// PhysicsFactory.createFixtureDef(density, mElasticity, mFriction, mSensor);//, false, mCategoryBit, mMaskBit, mGroupIndex);
		//fixtureDef.density = density;
		//fixtureDef.friction = mFriction;
		//fixtureDef.isSensor = mSensor;
		//fixtureDef.restitution = mElasticity;
		final BodyDef body = new BodyDef();
		body.type = mBodyType;
		mBody = mWorld.createBody(body);
		
		if (mFixtures != null) {
			for (FixtureData fixtureData : mFixtures) {
				Shape shape = null;
				
				if (fixtureData.bodyShape.equalsIgnoreCase("box")) {
					float sizeX = fixtureData.sizeX > 0 ? fixtureData.sizeX : mTransform.getSizeX();
					float sizeY = fixtureData.sizeY > 0 ? fixtureData.sizeY : mTransform.getSizeY();
					PolygonShape poly = new PolygonShape();
					poly.setAsBox((sizeX * 0.5f) / PIXEL_TO_METER_RATIO_DEFAULT, (sizeY * 0.5f) / PIXEL_TO_METER_RATIO_DEFAULT, new Vector2(fixtureData.x, fixtureData.y).div(PIXEL_TO_METER_RATIO_DEFAULT), 0.0f);
					shape = poly;
				} else if (fixtureData.bodyShape.equalsIgnoreCase("circle")) {
					CircleShape circ = new CircleShape();
					circ.setRadius(fixtureData.radius / PIXEL_TO_METER_RATIO_DEFAULT);
					shape = circ;
				}
				
				FixtureDef def = new FixtureDef();
				def.shape = shape;
				def.density = fixtureData.density;
				def.isSensor = fixtureData.sensor;
				
				Fixture fixture = mBody.createFixture(def);
				fixture.setUserData(fixtureData.userData);
			}
		} else {
			Shape shape = null;
			
			switch (mBodyShape) {
			case Box:
				PolygonShape poly = new PolygonShape();
				poly.setAsBox((mTransform.getSizeX() * 0.5f) / PIXEL_TO_METER_RATIO_DEFAULT, (mTransform.getSizeY() * 0.5f) / PIXEL_TO_METER_RATIO_DEFAULT);
				shape = poly;
				break;
			case Circle:
				//CircleShape circ = new CircleShape();
				//circ.setRadius(mTransform.getSize);
				//shape = circ;
				break;
			default:
				break;
			}
			
			FixtureDef def = new FixtureDef();
			def.shape = shape;
			def.density = density;
			def.isSensor = mSensor;

			Fixture fixture = mBody.createFixture(def);
			fixture.setUserData("");
		}
		
		mBody.setFixedRotation(mFixedRotation);
		mBody.setUserData(this);
	
		if (ignoreGravity) {
			mBody.setGravityScale(0.0f);
		}
	}
	
	@Override
	public void update() {
		updateTransform();
	}
	
	public boolean isSensor() {
		return mSensor;
	}
	
	public void updateTransform() {
		float xOffset = getAlignmentOffsetX();
		float yOffset = getAlignmentOffsetY();
		
		float xPosition = mBody.getPosition().x * PIXEL_TO_METER_RATIO_DEFAULT;
		float yPosition = mBody.getPosition().y * PIXEL_TO_METER_RATIO_DEFAULT;
		
		if (mTransform.getParent() != null) {
			xOffset -= mTransform.getParent().getWorldX();
			yOffset -= mTransform.getParent().getWorldY();
		}
		
		mTransform.setX(xPosition + xOffset);
		mTransform.setY(yPosition + yOffset);
		mTransform.setAngleZ(mBody.getAngle() * MathUtils.radiansToDegrees);
		
		mTransform.updateTransform(false);
	}
	
	@Override
	public void onTransformChanged() {
		super.onTransformChanged();
		
		//Util.DebugLog("Physics", "Rigid body on game object " + mGameObject.getId() + " updating it's transform.");
		
		if (mBody != null) {
			float xOffset = getAlignmentOffsetX();
			float yOffset = getAlignmentOffsetY();
			
			float bodyX = mTransform.getWorldX() - xOffset;
			float bodyY = mTransform.getWorldY() - yOffset;
			
			mBody.setTransform(bodyX / PIXEL_TO_METER_RATIO_DEFAULT, bodyY / PIXEL_TO_METER_RATIO_DEFAULT, mBody.getAngle());
		}
	}
	
	public void setLinearVelocity(float pX, float pY) {
		mBody.setLinearVelocity(pX, pY);
	}
	
	public void setPosition(float pX, float pY) {
		mBody.setTransform(pX, pY, mBody.getAngle());
	}
	
	public void applyTranslation(float pX, float pY) {
		Vector2 position = mBody.getPosition();
		setPosition(position.x + pX, position.y + pY);
	}
	
	public void applyLinearImpulse(float pX, float pY) {
		mBody.applyLinearImpulse(new Vector2(pX, pY), mBody.getWorldCenter());
	}
	
	public Vector2 getLinearVelocity() {
		return mBody.getLinearVelocity();
	}
	
	public float getAlignmentOffsetX() {
		float x = 0.0f;
		final Align align = mTransform.getAlign();
		
		if (align == Align.Left) {
			x -= mTransform.getSizeX() * 0.5f;
		} else if (align == Align.Center) {
		} else if (align == Align.Right) {
			x += mTransform.getSizeX() * 0.5f;
		}
		
		return x;
	}
	
	public float getAlignmentOffsetY() {
		float y = 0.0f;
		final Align align = mTransform.getAlignV();
		
		if (align == Align.Top) {
			y -= mTransform.getSizeY() * 0.5f;
		} else if (align == Align.Middle) {
		} else if (align == Align.Bottom) {
			y += mTransform.getSizeY() * 0.5f;
		}
		
		return y;
	}
	
	@Override
	public void onBeginCollision(RigidBody pRigidBody, Contact pContact) {
		for (Component component : mGameObject.getComponents()) {
			if (component != this) {
				component.onBeginCollision(pRigidBody, pContact);
			}
		}
	}

	@Override
	public void onEndCollision(RigidBody pRigidBody, Contact pContact) {
		for (Component component : mGameObject.getComponents()) {
			if (component != this) {
				component.onEndCollision(pRigidBody, pContact);
			}
		}
	}

	@Override
	public void onCollision(RigidBody pRigidBody, Contact pContact) {
		for (Component component : mGameObject.getComponents()) {
			if (component != this) {
				component.onCollision(pRigidBody, pContact);
			}
		}
	}
}

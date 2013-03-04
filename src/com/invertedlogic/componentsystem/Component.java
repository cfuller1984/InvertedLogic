package com.invertedlogic.componentsystem;

import java.lang.reflect.Field;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.invertedlogic.componentsystem.animation.Animation;
import com.invertedlogic.gameobject.GameObject;
import com.invertedlogic.gameobject.Transform;
import com.invertedlogic.physics.RigidBody;
import com.invertedlogic.util.Assert;

public abstract class Component {
	public static final int skInstanceType_Single = 0;
	public static final int skInstanceType_Multiple = 1;
	
	protected GameObject mGameObject;
	protected Transform mTransform;
	protected Material mMaterial;
	protected Animation mAnimation;
	
	protected boolean mEnabled = true;
	
	private int mInstanceType = skInstanceType_Single;
	
	public Component(GameObject pGameObject, int pInstanceType) {
		mGameObject = pGameObject;
		mTransform = mGameObject.getTransform();
		mMaterial = mGameObject.getMaterial();
		mAnimation = mGameObject.getAnimation();
		
		mInstanceType = pInstanceType;
	}
	
	protected void parseFromXml(Element pXmlNode) {
		NamedNodeMap attributes = pXmlNode.getAttributes();
		
		for (int i = 0; i < attributes.getLength(); ++i) {
			Node attribute = attributes.item(i);
			
			if (!parseAttributeFromXml(attribute.getNodeName(), attribute.getNodeValue()))
			{
				if (attribute.getNodeName().equalsIgnoreCase("enabled")) {
					setEnabled(Boolean.parseBoolean(attribute.getNodeValue()));
				} else {
					Assert.fail("Unexpected attribute.");
				}
			}
		}
	}
	
	protected boolean parseAttributeFromXml(String pName, String pValue)
	{
		boolean result = true;
		
		try
		{
			Field field = getClass().getDeclaredField(pName);
			field.setAccessible(true);
			
			Class<? extends Object> type = field.getType();
			try
			{
				if (type == float.class)
				{
					field.setFloat(this, Float.parseFloat(pValue));
				}
				else if (type == int.class)
				{
					field.setInt(this, Integer.parseInt(pValue));
				}
				else if (type == String.class)
				{
					field.set(this, pValue);
				}
				else if (type == boolean.class)
				{
					field.setBoolean(this, Boolean.parseBoolean(pValue));
				}
				else
				{
					// Unsupported data type
					Assert.fail("Unsupported data type.");
					result = false;
				}
			}
			catch (IllegalAccessException e)
			{
				result = false;
			}
			catch (IllegalArgumentException e)
			{
				result = false;
			}

			field.setAccessible(false);
		}
		catch (NoSuchFieldException e)
		{
			result = false;
		}
		
		return result;
	}
	
	abstract public void setup();
	
	public void inheritFrom(Component pComponent) {
		if (!pComponent.isEnabled()) {
			setEnabled(false);
		}
		/*
		for (Field field : getClass().getDeclaredFields()) {
			field.setAccessible(true);
			
			try
			{
				Field otherField = pComponent.getClass().getDeclaredField(field.getName());
				otherField.setAccessible(true);
				
				try
				{
					field.set(this, otherField.get(pComponent));
				}
				catch (IllegalAccessException e)
				{
					//continue;
				}
				catch (IllegalArgumentException e)
				{
					//continue;
				}
			}
			catch (NoSuchFieldException e)
			{
				//continue;
			}
			
			field.setAccessible(false);
		}
		*/
	}
	
	public void reset() {}
	abstract public void update();
	public void render() {}
	public void resize(int pWidth, int pHeight) {}
	
	abstract public void onDestroy();
	
	public int getInstanceType() {
		return mInstanceType;
	}
	
	public final void enable() {
		mEnabled = true;
		onEnabled();
	}
	
	public final void disable() {
		mEnabled = false;
		onDisabled();
	}
	
	public final void setEnabled(boolean pEnabled) {
		if (!mEnabled && pEnabled) {
			enable();
		} else if (mEnabled && !pEnabled){
			disable();
		}
	}
	
	public final boolean isEnabled() { return mEnabled; }
	
	public void onTransformChanged() {}
	public void onMaterialChanged() {}
	public void onVisibilityChanged() {}
	
	public void onAttached() {}
	public void onDetached() {}
	
	public void onAttachedToScene() {}
	public void onDetachedFromScene() {}
	
	public void onEnabled() {}
	public void onDisabled() {}
	
	public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
	public void touchDragged(int screenX, int screenY, int pointer) {}
	public void touchUp(int screenX, int screenY, int pointer, int button) {}
	
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { return false; }
	public void touchDragged(InputEvent event, float x, float y, int pointer) {}
	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {}
	
	public void onBeginCollision(RigidBody pRigidBody, Contact pContact) {}
	public void onCollision(RigidBody pRigidBody, Contact pContact) {}
	public void onEndCollision(RigidBody pRigidBody, Contact pContact) {}
	
	public GameObject getGameObject() { return mGameObject; }
	public Transform getTransform() { return mTransform; }
}

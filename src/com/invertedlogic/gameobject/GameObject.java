package com.invertedlogic.gameobject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.invertedlogic.componentsystem.Component;
import com.invertedlogic.componentsystem.ComponentFactory;
import com.invertedlogic.componentsystem.Material;
import com.invertedlogic.componentsystem.animation.Animation;
import com.invertedlogic.util.Assert;

public class GameObject {
	protected String mId;
	protected String mTag;
	protected int mLayerMask;
	
	protected boolean mVisible;
	protected boolean mEnabled;
	
	protected Transform mTransform;
	protected Animation mAnimation;
	protected Material mMaterial;
	protected BoundingBox mBoundingBox;
	
	protected ArrayList<Component> mComponents;
	
	public GameObject(GameObject pParent) {
		mId = "";
		mTag = "";
		
		mVisible = true;
		mEnabled = true;
		
		mComponents = new ArrayList<Component>();
		
		// Create the transform
		mTransform = new Transform(this);
		
		// Attach to the parent game object
		if (pParent != null) {
			pParent.getTransform().attachGameObject(this);
		}
		
		// Create a bounding box
		mBoundingBox = new BoundingBox();
	}
	
	public void onDestroy() {
		for (Component component : mComponents) {
			component.onDestroy();
		}
		
		if (mTransform.hasChildTransforms()) {
			for (Transform transform : mTransform.getChildTransforms()) {
				transform.getGameObject().onDestroy();
			}
		}
		
		mTransform.onDestroy();
	}
	
	public String getId() {
		return mId;
	}
	
	public String getTag() {
		return mTag;
	}
	
	protected void loadFromXml(Element pXmlNode) {
		// Initialise the GameObject from Xml
		parseFromXml(pXmlNode);
		
		// Initialise child GameObjects from Xml
		parseChildrenFromXml(pXmlNode);
	}
	
	protected void parseFromXml(Element pXmlNode) {
		// Inheritance
		if (pXmlNode.hasAttribute("inherit-from")) {
			GameObject inherit = GameObjectFactory.FindGameObjectInGameObjectLibrary(pXmlNode.getAttribute("inherit-from"));
			inheritFrom(inherit);
		}
		
		NamedNodeMap attributes = pXmlNode.getAttributes();
		
		Class<? extends GameObject> c = this.getClass();
		
		for (int i = 0; i < attributes.getLength(); ++i) {
			Node attribute = attributes.item(i);
			String name = attribute.getNodeName();
			
			try {
				Field field = c.getDeclaredField(name);
				Class<? extends Object> type = field.getType();
				
				try {
					if (type == float.class) {
						field.setFloat(this, Float.parseFloat(attribute.getNodeValue()));
					} else if (type == int.class) {
						field.setInt(this, Integer.parseInt(attribute.getNodeValue()));
					} else if (type == String.class) {
						field.set(this, attribute.getNodeValue());
					}
				} catch (IllegalAccessException e) {
				}
			}
			catch (NoSuchFieldException e)
			{
				// Field doesn't exist
				if (name.equalsIgnoreCase("inherit-from")) {
					// Handled above
				} else if (name.equalsIgnoreCase("id")) {
					mId = attribute.getNodeValue();
				} else if (name.equalsIgnoreCase("tag")) {
					mTag = attribute.getNodeValue();
				} else if (name.equalsIgnoreCase("visible")) {
					mVisible = Boolean.parseBoolean(attribute.getNodeValue());
				} else if (name.equalsIgnoreCase("enabled")) {
					mEnabled = Boolean.parseBoolean(attribute.getNodeValue());
				} else {
					Assert.fail("Unsupported attribute");
				}
			}
		}
	}
	
	protected void parseChildrenFromXml(Element pXmlNode) {
		
		// Generate a list of elements
		ArrayList<Element> elements = new ArrayList<Element>();
		
		NodeList list = pXmlNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				elements.add((Element)list.item(i));
			}
		}
		
		// Parse Transform
		{
			Iterator<Element> it = elements.iterator();
			while (it.hasNext()) {
				Element element = it.next();
				
				if (element.getNodeName().equalsIgnoreCase("Transform")) {
					mTransform.parseFromXml(element);
					if (!isLibraryGameObject()) {
						mTransform.setup();
					}
					it.remove();
				}
			}
		}
		
		// Parse Includes
		{
			Iterator<Element> it = elements.iterator();
			while (it.hasNext()) {
				Element element = it.next();
				
				if (element.getNodeName().equalsIgnoreCase("Include")) {
					GameObjectFactory.LoadGameObjectFromXml(element.getAttribute("file"), this);
					it.remove();
				}
			}
		}
		
		// Parse Game Objects
		{
			Iterator<Element> it = elements.iterator();
			while (it.hasNext()) {
				Element element = it.next();
				
				if (element.getNodeName().equalsIgnoreCase("GameObject")) {
					GameObjectFactory.LoadGameObjectInternal(element, this);
					it.remove();
				}
			}
		}
		
		// Parse Components
		{
			Iterator<Element> it = elements.iterator();
			while (it.hasNext()) {
				Element element = it.next();
				
				Component component = ComponentFactory.LoadComponentInternal(element, this);
				if (component.getClass() == Material.class) {
					mMaterial = (Material)component;
				} else if (component.getClass() == Animation.class) {
					mAnimation = (Animation)component;
				}
				
				it.remove();
			}
		}
	}
	
	protected void inheritFrom(GameObject pGameObject) {
		mId = pGameObject.mId;
		mTag = pGameObject.mTag;
		mVisible = pGameObject.mVisible;
		mEnabled = pGameObject.mEnabled;
		
		// Inherit the transform
		mTransform.inheritFrom(pGameObject.getTransform());
		if (!isLibraryGameObject()) {
			mTransform.setup();
		}
		
		// Inherit game objects
		inheritGameObjectsFrom(pGameObject);
		
		// Inherit components
		inheritComponentsFrom(pGameObject);
	}
	
	protected void inheritComponentsFrom(GameObject pGameObject) {
		for (Component inheritComponent : pGameObject.getComponents()) {
			
			Component component = ComponentFactory.CreateComponent(inheritComponent.getClass(), this);
			if (component.getClass() == Material.class) {
				mMaterial = (Material)component;
			}
			component.inheritFrom(inheritComponent);
		}
	}
	
	protected void inheritGameObjectsFrom(GameObject pGameObject) {
		if (pGameObject.getTransform().hasChildTransforms()) {
			for (Transform transform : pGameObject.getTransform().getChildTransforms()) {
				GameObject inheritGameObject = transform.getGameObject();
				
				GameObject gameObject = new GameObject(this);
				gameObject.inheritFrom(inheritGameObject);
				
				//if (!isLibraryGameObject()) {
				//	gameObject.setup();
				//}
			}
		}
	}
	
	public void setup() {
		mAnimation = (Animation) getComponentOfType(Animation.class);
		mMaterial = (Material) getComponentOfType(Material.class);
		
		// Setup child game objects
		if (mTransform.hasChildTransforms()) {
			for (Transform transform : mTransform.getChildTransforms()) {
				transform.getGameObject().setup();
			}
		}
		
		// Setup components
		for (Component component : mComponents) {
			component.setup();
		}
		
		if (mMaterial != null) {
			onMaterialChanged(false);
		}
		
		onTransformChanged(false);
		onVisibilityChanged();
	}
	
	public void update() {
		if (isEnabled()) {
			mTransform.update();
			
			//Assert.assertTrue(isEnabled());
			
			// Update components
			for (Component component : mComponents) {
				if (component.isEnabled()) {
					component.update();
				}
			}
			
			// Update child game objects
			if (mTransform.hasChildTransforms()) {
				for (Iterator<Transform> it = mTransform.getChildTransforms().iterator(); it.hasNext();) {
					Transform transform = it.next();
					transform.getGameObject().update();
				}
			}
		}
	}
	
	public void render() {
		for (Iterator<Component> it = mComponents.iterator(); it.hasNext();) {
			Component component = it.next();
			component.render();
		}
		
		if (mTransform.hasChildTransforms()) {
			for (Iterator<Transform> it = mTransform.getChildTransforms().iterator(); it.hasNext();) {
				Transform transform = it.next();
				transform.getGameObject().render();
			}
		}
	}
	
	public void setLayerMask(int pLayerId) {
		mLayerMask |= pLayerId;
	}
	
	public void unsetLayerMask(int pLayerId) {
		mLayerMask &=~pLayerId;
	}
	
	public boolean isLayerMaskSet(int pLayerId) {
		return mLayerMask == 0 || (mLayerMask & pLayerId) != 0;
	}
	
	public boolean isLibraryGameObject() {
		Transform transform = mTransform;
		
		while (transform != null) {
			if (transform.getGameObject().getClass() == GameObjectLibrary.class) {
				return true;
			}
			transform = transform.getParent();
		}
		
		return false;
	}
	
	public void addToStage(Stage pStage) {
		mTransform.addToStage(pStage);
	}
	
	public void onAttachedToScene() {
		for (Component component : mComponents) {
			component.onAttachedToScene();
		}
		
		if (mTransform.hasChildTransforms()) {
			for (Transform transform : mTransform.getChildTransforms()) {
				transform.getGameObject().onAttachedToScene();
			}
		}
	}
	
	public void attachComponent(Component pComponent) {
		mComponents.add(pComponent);
	}
	
	public void detachComponent(Component pComponent) {
		mComponents.remove(pComponent);
	}
	
	public Transform getTransform() {
		return mTransform;
	}
	
	public Material getMaterial() {
		return mMaterial;
	}
	
	public Animation getAnimation() {
		return mAnimation;
	}
	
	public void playAnimation(String pId, boolean pPlayOnChildren) {
		if (mAnimation != null) {
			mAnimation.play(pId, false);
		}
		
		if (pPlayOnChildren
				&& mTransform.hasChildTransforms())
		{
			for (Transform transform : mTransform.getChildTransforms()) {
				transform.getGameObject().playAnimation(pId, true);
			}
		}
	}
	
	public GameObject findChild(String pId) {
		Transform transform = mTransform.findChildTransform(pId);
		if (transform != null) {
			return transform.getGameObject();
		}
		
		return null;
	}
	
	public boolean childExists(String pId) {
		try {
			Transform transform = mTransform.findChildTransform(pId);
			return transform != null;
		} catch (Exception e){
			return false;
		}
	}
	
	public GameObject getChild(int pIndex) {
		return mTransform.getChildTransform(pIndex).getGameObject();
	}
	
	public int getChildCount() {
		return mTransform.getChildTransformCount();
	}
	
	public Component getComponentOfType(Class<?> pClass) {
		for (Component component : mComponents) {
			Class<?> componentClass = component.getClass();
			Class<?> requestedClass = pClass;
			
			while (componentClass != null) {
				Class<?> superClass = componentClass.getSuperclass();
				
				if (superClass != null
						&& superClass != Component.class) {
					componentClass = superClass;
				} else {
					break;
				}
			}
			
			while (requestedClass != null) {
				Class<?> superClass = requestedClass.getSuperclass();
				
				if (superClass != null
						&& superClass != Component.class) {
					requestedClass = superClass;
				} else {
					break;
				}
			}
			
			if (componentClass == requestedClass) {
				return component;
			}
		}
		
		return null;
	}
	
	public ArrayList<Component> getComponents() {
		return mComponents;
	}
	
	public boolean isVisible() {
		boolean visible = mVisible;
		
		if (mTransform.getParent() != null) {
			visible &= mTransform.getParent().getGameObject().isVisible();
		}
		
		return visible;
	}
	
	public void setVisible(final boolean pVisible) {
		//boolean wasVisible = mVisible;
		mVisible = pVisible;
		
		onVisibilityChanged();
	}
	
	public boolean isEnabled() {
		boolean enabled = mEnabled;
		
		if (mTransform.getParent() != null) {
			enabled &= mTransform.getParent().getGameObject().isEnabled();
		}
		
		return enabled;
	}
	
	public void setEnabled(final boolean pEnabled) {
		//boolean wasVisible = mVisible;
		mEnabled = pEnabled;
		
		if (mEnabled) {
			onEnabled();
		} else {
			onDisabled();
		}
	}
	
	public float getSizeX() {
		return mTransform.getSizeX();
	}
	
	public float getSizeY() {
		return mTransform.getSizeY();
	}
	
	public BoundingBox getBoundingBox() {
		return mBoundingBox;
	}
	
	protected void onVisibilityChanged() {
		mTransform.setVisible(isVisible());
		
		for (Component component : mComponents) {
			component.onVisibilityChanged();
		}
		
		if (mTransform.getChildTransformCount() > 0) {
			for (Transform transform : mTransform.getChildTransforms()) {
				transform.getGameObject().onVisibilityChanged();
			}
		}
	}
	
	protected void onEnabled() {
		mTransform.setEnabled(true);
		
		for (Component component : mComponents) {
			if (component.isEnabled()) {
				component.onEnabled();
			}
		}
		
		if (mTransform.getChildTransformCount() > 0) {
			for (Transform transform : mTransform.getChildTransforms()) {
				transform.getGameObject().onEnabled();
			}
		}
	}
	
	protected void onDisabled() {
		mTransform.setEnabled(false);
		
		for (Component component : mComponents) {
			component.onDisabled();
		}
		
		if (mTransform.getChildTransformCount() > 0) {
			for (Transform transform : mTransform.getChildTransforms()) {
				transform.getGameObject().onDisabled();
			}
		}
	}
	
	public void onTransformChanged(boolean pApplyToChildren) {
		/*
		//setRotationCenter(mTransform.mWidth * 0.5f, mTransform.mHeight * 0.5f);
		float fOffsetX = 0.0f;
		float fOffsetY = 0.0f;
		
		float fScaleX = 1.0f;
		float fScaleY = 1.0f;
		
		// Multiply the size by all parent components sizes
		if (mTransform.mParent != null) {
			Transform parent = mTransform.mParent;
			
			while (parent != null) {
				fScaleX *= parent.getGameObject().getSizeX();
				fScaleY *= parent.getGameObject().getSizeY();
				parent = parent.mParent;
			}
			
			fOffsetX += mTransform.mParent.getGameObject().getClientX();
			fOffsetY += mTransform.mParent.getGameObject().getClientY();
			
			mTransform.setX((mTransform.getX() * fScaleX) + fOffsetX);
			mTransform.setY((mTransform.getY() * fScaleY) + fOffsetY);
		}*/
		
		// Apply to child components
		if (pApplyToChildren
				&& mTransform.getChildTransformCount() > 0) {
			for (Transform transform : mTransform.getChildTransforms()) {
				transform.getGameObject().onTransformChanged(true);
			}
		}
		
		// Apply to components
		for (Component component : mComponents) {
			component.onTransformChanged();
		}
	}
	
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// Send the event to components of this game object
		for (Iterator<Component> it = mComponents.iterator(); it.hasNext();) {
			Component component = it.next();
			
			if (component.isEnabled()
					&& component.touchDown(screenX, screenY, pointer, button)) {
				return true;
			}
		}
		
		// Send the event to child transforms
		if (mTransform.hasChildTransforms()) {
			for (Iterator<Transform> it = mTransform.getChildTransforms().iterator(); it.hasNext();) {
				Transform transform = it.next();
				if (transform.getGameObject().isEnabled()
						&& transform.getGameObject().touchDown(screenX, screenY, pointer, button)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void touchDragged(int screenX, int screenY, int pointer) {
		// Send the event to components of this game object
		for (Iterator<Component> it = mComponents.iterator(); it.hasNext();) {
			Component component = it.next();
			if (component.isEnabled()) {
				component.touchDragged(screenX, screenY, pointer);
			}
		}
		
		// Send the event to child transforms
		if (mTransform.hasChildTransforms()) {
			for (Iterator<Transform> it = mTransform.getChildTransforms().iterator(); it.hasNext();) {
				Transform transform = it.next();
				if (transform.getGameObject().isEnabled()) {
					transform.getGameObject().touchDragged(screenX, screenY, pointer);
				}
			}
		}
	}
	
	public void touchUp(int screenX, int screenY, int pointer, int button) {
		// Send the event to components of this game object
		for (Iterator<Component> it = mComponents.iterator(); it.hasNext();) {
			Component component = it.next();
			if (component.isEnabled()) {
				component.touchUp(screenX, screenY, pointer, button);
			}
		}
		
		// Send the event to child transforms
		if (mTransform.hasChildTransforms()) {
			for (Iterator<Transform> it = mTransform.getChildTransforms().iterator(); it.hasNext();) {
				Transform transform = it.next();
				if (transform.getGameObject().isEnabled()) {
					transform.getGameObject().touchUp(screenX, screenY, pointer, button);
				}
			}
		}
	}
	
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		// Send the event to components of this game object
		for (Iterator<Component> it = mComponents.iterator(); it.hasNext();) {
			Component component = it.next();
			if (component.isEnabled()
					&& component.touchDown(event, x, y, pointer, button)) {
				return true;
			}
		}
		
		// Send the event to child transforms
		if (mTransform.hasChildTransforms()) {
			for (Iterator<Transform> it = mTransform.getChildTransforms().iterator(); it.hasNext();) {
				Transform transform = it.next();
				if (transform.getGameObject().isEnabled()
						&& transform.getGameObject().touchDown(event, x, y, pointer, button)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void touchDragged(InputEvent event, float x, float y, int pointer) {
		// Send the event to components of this game object
		for (Iterator<Component> it = mComponents.iterator(); it.hasNext();) {
			Component component = it.next();
			if (component.isEnabled()) {
				component.touchDragged(event, x, y, pointer);
			}
		}
		
		// Send the event to child transforms
		if (mTransform.hasChildTransforms()) {
			for (Iterator<Transform> it = mTransform.getChildTransforms().iterator(); it.hasNext();) {
				Transform transform = it.next();
				if (transform.getGameObject().isEnabled()) {
					transform.getGameObject().touchDragged(event, x, y, pointer);
				}
			}
		}
	}
	
	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		// Send the event to components of this game object
		for (Iterator<Component> it = mComponents.iterator(); it.hasNext();) {
			Component component = it.next();
			if (component.isEnabled()) {
				component.touchUp(event, x, y, pointer, button);
			}
		}
		
		// Send the event to child transforms
		if (mTransform.hasChildTransforms()) {
			for (Iterator<Transform> it = mTransform.getChildTransforms().iterator(); it.hasNext();) {
				Transform transform = it.next();
				if (transform.getGameObject().isEnabled()) {
					transform.getGameObject().touchUp(event, x, y, pointer, button);
				}
			}
		}
	}
	
	public void onMaterialChanged(boolean pApplyToChildren) {
		// Apply to children
		if (pApplyToChildren
				&& mTransform.hasChildTransforms()) {
			for (Transform transform : mTransform.getChildTransforms()) {
				transform.getGameObject().onMaterialChanged(true);
			}
		}
		
		// Apply to components
		for (Component component : mComponents) {
			component.onMaterialChanged();
		}
	}
	
	public void onAngleChanged() {
	}
	
	public BoundingBox calculateBoundingBox() {
		mBoundingBox.reset();
		mBoundingBox.expand(mTransform.getWorldAlignedX(), mTransform.getWorldAlignedY(), mTransform.getWorldAlignedX() + mTransform.getSizeX(), mTransform.getWorldAlignedY() + mTransform.getSizeY());
		
		if (mTransform.hasChildTransforms()) {
			for (Transform transform : mTransform.getChildTransforms()) {
				mBoundingBox.expand(transform.getGameObject().calculateBoundingBox());
			}
		}
		
		return mBoundingBox;
	}
}

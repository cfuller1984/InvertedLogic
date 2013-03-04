package com.invertedlogic.gameobject;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.invertedlogic.componentsystem.Component;
import com.invertedlogic.util.Align;
import com.invertedlogic.util.Assert;

public class Transform extends Component {
	TransformGroup mActor;
	
	float mX;
	float mY;
	
	float mSizeX;
	float mSizeY;
	
	float mScaleX;
	float mScaleY;
	
	float mRotationZ;
	
	Align mAlign;
	Align mAlignV;
	
	Transform mParent;
	protected ArrayList<Transform> mChildTransforms;
	
	boolean mTransformDirty;
	
	public Transform(GameObject pGameObject) {
		super(pGameObject, Component.skInstanceType_Single);
		
		mScaleX = 1.0f;
		mScaleY = 1.0f;
		
		mActor = new TransformGroup(this);
		mActor.setZIndex(1);
		
		mTransformDirty = true;
	}
	
	public void onDestroy() {
		mActor = null;
	}
	
	protected void parseFromXml(Element pXmlNode) {
		NamedNodeMap attributes = pXmlNode.getAttributes();
		
		for (int i = 0; i < attributes.getLength(); ++i) {
			Node attribute = attributes.item(i);
			String name = attribute.getNodeName();
			
			if (name.equalsIgnoreCase("x")) {
				mX = Float.valueOf(attribute.getNodeValue()).floatValue();
			} else if (name.equalsIgnoreCase("y")) {
				mY = Float.valueOf(attribute.getNodeValue()).floatValue();
			} else if (name.equalsIgnoreCase("size-x")) {
				mSizeX = Float.valueOf(attribute.getNodeValue()).floatValue();
			} else if (name.equalsIgnoreCase("size-y")) {
				mSizeY = Float.valueOf(attribute.getNodeValue()).floatValue();
			} else if (name.equalsIgnoreCase("rotation-z")) {
				mRotationZ = Float.valueOf(attribute.getNodeValue()).floatValue();
			} else if (name.equalsIgnoreCase("align")) {
				mAlign = Align.getAlign(attribute.getNodeValue());
			} else if (name.equalsIgnoreCase("valign")) {
				mAlignV = Align.getAlign(attribute.getNodeValue());
			} else {
				Assert.fail("Unsupported attribute");
			}
		}
	}
	
	@Override
	public void inheritFrom(Component pComponent) {
		super.inheritFrom(pComponent);
		
		Transform transform = (Transform)pComponent;
		
		mX = transform.mX;
		mY = transform.mY;
		mSizeX = transform.mSizeX;
		mSizeY = transform.mSizeY;
		mRotationZ = transform.mRotationZ;
		mAlign = transform.mAlign;
		mAlignV = transform.mAlignV;
	}

	@Override
	public void setup() {
		float x = 0.0f;
		float y = 0.0f;
		
		if (mAlign == Align.Left) {
		} else if (mAlign == Align.Center) {
			x += getSizeX() * 0.5f;
		} else if (mAlign == Align.Right) {
			x += getSizeX();
		}
		
		if (mAlignV == Align.Top) {
		} else if (mAlignV == Align.Middle) {
			y += getSizeY() * 0.5f;
		} else if (mAlignV == Align.Bottom) {
			y += getSizeY();
		}
		
		mActor.setOrigin(x, y);
		
		onTransformChanged();
	}

	@Override
	public void reset() {
	}
	
	@Override
	public void update() {
		if (mTransformDirty) {
			updateTransform(true);
		}
		
		// Update child transforms
		if (mChildTransforms != null) {
			for (Iterator<Transform> it = mChildTransforms.iterator(); it.hasNext();) {
				Transform t = it.next();
				t.update();
			}
		}
	}
	
	@Override
	public void onEnabled() {
		//mActor.setIgnoreUpdate(false);
	}
	
	@Override
	public void onDisabled() {
		//mActor.setIgnoreUpdate(true);
	}
	
	public void addToStage(Stage pStage) {
		pStage.addActor(mActor);
		mGameObject.onAttachedToScene();
	}
	
	public void addActor(Actor pActor) {
		mActor.addActor(pActor);
	}
	
	public void removeActor(Actor pActor) {
		mActor.removeActor(pActor);
	}
	
	public void addComponentActor(Actor pActor) {
		mActor.addActorAt(0, pActor);
	}
	
	public Actor getActor() {
		return mActor;
	}
	
	public void attachGameObject(GameObject pGameObject) {
		attachChildTransform(pGameObject.getTransform());
	}
	
	public void detachGameObject(GameObject pGameObject) {
		detachChildTransform(pGameObject.getTransform());
	}
	
	public void attachChildTransform(Transform pTransform) {
		addActor(pTransform.getActor());
		
		if (mChildTransforms == null) {
			mChildTransforms = new ArrayList<Transform>();
		}
		
		pTransform.mParent = this;
		mChildTransforms.add(pTransform);
	}
	
	public void detachChildTransform(Transform pTransform) {
		// Make sure the child transform is attached to this transform
		Assert.assertTrue(pTransform.mParent == this);
		
		// Detach the entity
		removeActor(pTransform.getActor());
		
		// Remove the transform
		pTransform.mParent = null;
		mChildTransforms.remove(pTransform);
	}
	
	public Transform getParent() {
		return mParent;
	}
	
	public ArrayList<Transform> getChildTransforms() {
		return mChildTransforms;
	}
	
	public Transform getChildTransform(int pIndex) {
		Assert.assertNotNull(mChildTransforms);
		return mChildTransforms.get(pIndex);
	}
	
	public boolean hasChildTransforms() {
		return mChildTransforms != null;
	}
	
	public int getChildTransformCount() {
		return mChildTransforms != null ? mChildTransforms.size() : 0;
	}
	
	public GameObject findChildGameObject(String pId) {
		Transform transform = findChildTransform(pId);
		return transform.getGameObject();
	}
	
	public Transform findChildTransform(String pId) {
		Transform transform = null;
		
		final String token = "::";
		int tokenLength = token.length();
		
		String searchName = pId;
		int searchNameEnd = pId.indexOf(token);
		
		if (searchNameEnd >-1) {
			searchName = searchName.substring(0, searchNameEnd);
			pId = pId.substring(searchNameEnd + tokenLength);
		}
		
		if (mChildTransforms != null) {
			for (Transform child : mChildTransforms) {
				if (child.mGameObject.getId() != null && child.mGameObject.getId().equalsIgnoreCase(searchName))
				{
					transform = child;
					break;
				}/* else if (child.getId() == null
						&& child.findComponent(searchName, false) != null) {
					component = child.findComponent(searchName);
					if (component != null) {
						break;
					}
				}*/
			}
		}
		
		if (transform != null
				&& searchNameEnd >-1) {
			transform = transform.findChildTransform(pId);
		}
		
		//Assert.assertNotNull(transform);
		
		return transform;
	}
	
	public void setVisible(boolean pVisible) {
		mActor.setVisible(pVisible);
		//mActor.setIgnoreUpdate(!pVisible);
		//mActor.setVisible(pVisible);
	}
	
	public float getX() {
		return mX;
	}
	
	public float getY() {
		return mY;
	}
	
	public Vector2 getPosition() {
		return new Vector2(mX, mY);
	}
	
	public float getCenterX() {
		float x = mX;
		
		if (mAlign == Align.Left) {
			x += getSizeX() * 0.5f;
		} else if (mAlign == Align.Center) {
		} else if (mAlign == Align.Right) {
			x += getSizeX() *-0.5f;
		}
		
		return x;
	}
	
	public float getCenterY() {
		float y = mY;
		
		if (mAlignV == Align.Top) {
			y += getSizeY() * 0.5f;
		} else if (mAlignV == Align.Middle) {
		} else if (mAlignV == Align.Bottom) {
			y += getSizeY() *-0.5f;
		}
		
		return y;
	}
	
	public float getWorldX() {
		float x = getX();
		
		if (mParent != null) {
			x += mParent.getWorldX();
		}
		
		return x;
	}
	
	public float getWorldY() {
		float y = getY();
		
		if (mParent != null) {
			y += mParent.getWorldY();
		}
		
		return y;
	}
	
	public Vector2 getWorldPosition() {
		return new Vector2(getWorldX(), getWorldY());
	}
	
	public void setX(float pX) {
		mX = pX;
		mTransformDirty = true;
	}
	
	public void setY(float pY) {
		mY = pY;
		mTransformDirty = true;
	}
	
	public void setAngleZ(float pAngle) {
		mRotationZ = pAngle;
		mTransformDirty = true;
	}
	
	public float getSizeX() {
		return mSizeX;
	}
	
	public float getSizeY() {
		return mSizeY;
	}
	
	public float getScaleX() {
		return mScaleX;
	}
	
	public float getScaleY() {
		return mScaleY;
	}
	
	public Align getAlign() {
		return mAlign;
	}
	
	public Align getAlignV() {
		return mAlignV;
	}
	
	public float getAlignedX() {
		return getX() + getAlignmentOffsetX();
	}
	
	public float getAlignedY() {
		return getY() + getAlignmentOffsetY();
	}
	
	public float getWorldAlignedX() {
		return getWorldX() + getAlignmentOffsetX();
	}
	
	public float getWorldAlignedY() {
		return getWorldY() + getAlignmentOffsetY();
	}
	
	public float getAlignmentOffsetX() {
		float x = 0.0f;
		
		if (mAlign == Align.Left) {
		} else if (mAlign == Align.Center) {
			x -= getSizeX() * 0.5f;
		} else if (mAlign == Align.Right) {
			x -= getSizeX();
		}
		
		return x;
	}
	
	public float getAlignmentOffsetY() {
		float y = 0.0f;
		
		if (mAlignV == Align.Top) {
		} else if (mAlignV == Align.Middle) {
			y -= getSizeY() * 0.5f;
		} else if (mAlignV == Align.Bottom) {
			y -= getSizeY();
		}
		
		return y;
	}
	
	public float getAngleZ() {
		return mRotationZ;
	}
	
	//public float getWorldAngleZ() {
	//	
	//}
	
	public float getApproximateRadius() {
		return ((mSizeX + mSizeY) * 0.25f);
	}
	
	public boolean isTransformDirty() {
		return mTransformDirty;
	}
	
	public void setTransformDirty(boolean dirty) {
		mTransformDirty = dirty;
	}
	
	public void updateTransform(boolean applyToChildren) {
		float x = getX();
		float y = getY();
		
		float offsetX = getAlignmentOffsetX();// * FloatMath.sin(mRotationZ);
		float offsetY = getAlignmentOffsetY();// *-FloatMath.cos(mRotationZ);
		
		mActor.setSize(getSizeX(), getSizeY());
		mActor.setPosition(x + offsetX, y + offsetY);
		mActor.setRotation(mRotationZ);
		
		if (applyToChildren) {
			mGameObject.onTransformChanged(true);
		}
		
		mTransformDirty = false;
	}
	
	public void onTransformChanged() {
		mGameObject.calculateBoundingBox();
	}
	
	public boolean collidesWithTransform(Transform pTransform) {
		float distance = new Vector2(getWorldAlignedX(), getWorldAlignedY()).dst(pTransform.getWorldAlignedX(), pTransform.getWorldAlignedY());
		return distance < getApproximateRadius() + pTransform.getApproximateRadius();
	}
}

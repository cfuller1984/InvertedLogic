package com.invertedlogic.scene;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.invertedlogic.assets.TextureFactory;
import com.invertedlogic.componentsystem.CameraComponent;
import com.invertedlogic.gameobject.GameObject;
import com.invertedlogic.gameobject.GameObjectFactory;
import com.invertedlogic.input.InputManager;

public class Scene extends Stage {
	// ===========================================================
	// Constants
	// ===========================================================
	
	// ===========================================================
	// Fields
	// ===========================================================
	static Scene smCurrentScene;
	public static CameraComponent Camera;
	

	GameObject mRoot;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	public Scene() {
		super(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, TextureFactory.Get().getSpriteBatch());
		
		InputManager inputManager = InputManager.Get();
		inputManager.addInputProcessor(this);
		
		mRoot = GameObjectFactory.CreateGameObject(null);
		addActor(mRoot.getTransform().getActor());
	}
	
	public void resize(int pWidth, int pHeight) {
		setViewport(pWidth, pHeight, true);
	}

	public void update() {
		act(Gdx.graphics.getDeltaTime());

		mRoot.update();
	}
	
	public void render() {
		mRoot.render();
	}
	
	public void draw(CameraComponent pCamera) {
		Camera = pCamera;
		setCamera(pCamera.getCamera());
		super.draw();
	}
	
	public void loadFromXml(Element pXmlRoot) {
		// Generate a list of elements
		ArrayList<Element> elements = new ArrayList<Element>();
		
		NodeList list = pXmlRoot.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				elements.add((Element)list.item(i));
			}
		}
		
		// Parse Game Objects
		{
			Iterator<Element> it = elements.iterator();
			while (it.hasNext()) {
				Element element = it.next();
				
				if (element.getNodeName().equalsIgnoreCase("GameObject")) {
					GameObjectFactory.LoadGameObjectInternal(element, mRoot);
				}
			}
		}
	}
	
	public void setup() {
		mRoot.calculateBoundingBox();
		mRoot.setup();
	}
	
	public void dispose() {
		mRoot.onDestroy();
	}
	
	public GameObject Root() {
		return mRoot;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		super.touchDown(screenX, screenY, pointer, button);
		
		mRoot.touchDown(screenX, screenY, pointer, button);
		
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		super.touchDragged(screenX, screenY, pointer);
		
		// Distribute to game objects in the scene
		mRoot.touchDragged(screenX, screenY, pointer);
		
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		super.touchUp(screenX, screenY, pointer, button);
		
		// Distribute to game objects in the scene
		mRoot.touchUp(screenX, screenY, pointer, button);
		
		return false;
	}
	
	// ===========================================================
	// Static Methods
	// ===========================================================
	public static void SetCurrentScene(Scene pScene) {
		if (smCurrentScene != null) {
			smCurrentScene.dispose();
		}
		
		smCurrentScene = pScene;
		pScene.setup();
	}
	
	public static Scene GetCurrentScene() {
		return smCurrentScene;
	}
	
	public static GameObject FindGameObject(String pId) {
		return smCurrentScene.mRoot.findChild(pId);
	}
}

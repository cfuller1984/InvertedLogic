package com.invertedlogic.gameobject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.invertedlogic.util.Assert;

public class GameObjectFactory {
	static HashMap<String, GameObjectLibrary> smGameObjectLibraries = new HashMap<String, GameObjectLibrary>();
		
	static public void LoadGameObjectLibraryFromXml(String pFilename) {
		GameObjectLibrary library = null;
		
		FileHandle fh = Gdx.files.internal(pFilename);
		
		if (fh.exists()) {
			Document document = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			InputStream is = fh.read();
			
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				document = db.parse(is);
			} catch (ParserConfigurationException e) {
				Assert.fail("Error parsing Xml document.");
			} catch (IllegalArgumentException e) {
				Assert.fail("Error parsing Xml document.");
			} catch (SAXException e) {
				Assert.fail("Error parsing Xml document.");
			} catch (IOException e) {
				Assert.fail("Error parsing Xml document.");
			}
			
			Element rootNode = document.getDocumentElement();
			
			library = new GameObjectLibrary();
			
			Assert.assertTrue(rootNode.hasAttribute("id"));
			String key = rootNode.getAttribute("id");
			
			smGameObjectLibraries.put(key, library);
			
			// Initialise the GameObject from Xml
			library.parseFromXml(rootNode);
			
			// Initialise child GameObjects from Xml
			library.parseChildrenFromXml(rootNode);
		}
		
		Assert.assertNotNull("Error loading GameObject library from Xml file " + pFilename, library);
	}
	
	static public GameObject FindGameObjectInGameObjectLibrary(String pId) {
		GameObject gameObject = null;
		
		for (GameObjectLibrary library : smGameObjectLibraries.values()) {
			gameObject = library.findChild(pId);
			if (gameObject != null) break;
		}
		
		return gameObject;
	}
	
	static public GameObject CreateGameObject(GameObject pParent) {
		// Create a new game object
		return new GameObject(pParent);
	}
	
	static public GameObject InheritGameObject(String pId, GameObject pParent) {
		GameObject gameObject = CreateGameObject(pParent);
		
		GameObject inheritGameObject = FindGameObjectInGameObjectLibrary(pId);
		gameObject.inheritFrom(inheritGameObject);
		gameObject.setup();
		
		return gameObject;
	}
	
	static public void DestroyGameObject(GameObject pGameObject) {
		Transform parent = pGameObject.getTransform().getParent();
		
		// detach from the parent transform
		if (parent != null) {
			parent.detachGameObject(pGameObject);
		}
		
		pGameObject.onDestroy();
	}
	
	static public GameObject LoadGameObjectFromXml(String pFilename, GameObject pParent) {
		GameObject gameObject = null;
		
		FileHandle fh = Gdx.files.internal(pFilename);
		
		if (fh.exists()) {
			Document document = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			InputStream is = fh.read();
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				document = db.parse(is);
			} catch (IllegalArgumentException e) {
			} catch (ParserConfigurationException e) {
			} catch (SAXException e) {
			} catch (IOException e) {
			}
			
			Element rootNode = document.getDocumentElement();
			gameObject = LoadGameObjectInternal(rootNode, pParent);
			if (!gameObject.isLibraryGameObject()) {
				gameObject.calculateBoundingBox();
				gameObject.setup();
			}
			
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		
		Assert.assertNotNull(gameObject);
		return gameObject;
	}
	
	static public GameObject LoadGameObjectInternal(Element pXmlNode, GameObject pParent) {
		GameObject gameObject = null;
		
		// If this is an include, load the GameObject from Xml, otherwise
		// create the GameObject.
		if (pXmlNode.getNodeName().equalsIgnoreCase("include")) {
			return LoadGameObjectFromXml(pXmlNode.getAttribute("file"), pParent);
		}
		else {
			gameObject = CreateGameObject(pParent);
			Assert.assertNotNull(gameObject);
			
			gameObject.loadFromXml(pXmlNode);
		}
		
		return gameObject;
	}
}

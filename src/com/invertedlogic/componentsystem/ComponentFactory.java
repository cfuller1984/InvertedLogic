package com.invertedlogic.componentsystem;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.invertedlogic.componentsystem.animation.Animation;
import com.invertedlogic.gameobject.GameObject;
import com.invertedlogic.gameobject.Transform;
import com.invertedlogic.particlesystem.ParticleSystem;
import com.invertedlogic.physics.PhysicsWorldComponent;
import com.invertedlogic.physics.RigidBody;
import com.invertedlogic.util.Assert;

public class ComponentFactory {
	static HashMap<String, Class<? extends Component>> smComponentTypes = new HashMap<String, Class<? extends Component>>();
	static HashMap<String, ComponentLibrary> smComponentLibraries = new HashMap<String, ComponentLibrary>();
	
	static public void RegisterComponentType(Class<? extends Component> pClass) {
		Assert.assertFalse("Class already registered.", smComponentTypes.containsKey(pClass.getSimpleName()));
		
		String simpleName = pClass.getSimpleName().toLowerCase(Locale.ENGLISH);
		if (!smComponentTypes.containsKey(simpleName)) {
			smComponentTypes.put(simpleName, pClass);
		}
	}
	
	static public void RegisterComponentType(String pClassName, Class<? extends Component> pClass) {
		if (!smComponentTypes.containsKey(pClassName)) {
			smComponentTypes.put(pClassName, pClass);
		}
	}
	
	static public void RegisterBuiltInComponentTypes() {
		RegisterComponentType(ComponentLibrary.class);
		RegisterComponentType(Component.class);

		RegisterComponentType(Animation.class);
		RegisterComponentType(Script.class);
		RegisterComponentType(Transform.class);
		RegisterComponentType(Material.class);
		RegisterComponentType(BoxCollider.class);
		RegisterComponentType(TouchCollider.class);
		RegisterComponentType(ParticleSystem.class);
		
		RegisterComponentType("camera", CameraComponent.class);
		
		RegisterComponentType("quad", QuadComponent.class);
		RegisterComponentType("text", TextComponent.class);
		
		// Physics
		RegisterComponentType("physicsworld", PhysicsWorldComponent.class);
		RegisterComponentType("rigidbody", RigidBody.class);
	}
	
	static public void LoadComponentLibraryFromXml(String pFilename) {
		ComponentLibrary library = null;
		
		FileHandle fh = Gdx.files.internal(pFilename);
		
		if (fh.exists()) {
			Document document = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				document = db.parse(fh.file());
			} catch (ParserConfigurationException e) {
			} catch (SAXException e) {
			} catch (IOException e) {
			}
			
			Element rootNode = document.getDocumentElement();
			library = (ComponentLibrary)ComponentFactory.LoadComponentInternal(rootNode, null);
		}
		
		Assert.assertNotNull("Error loading component library from Xml file " + pFilename, library);
		smComponentLibraries.put(library.getId(), library);
	}
	
	static public Component FindComponentInComponentLibrary(String pId) {
		Component component = null;
		/*
		for (ComponentLibrary library : smComponentLibraries.values()) {
			component = library.findChild(pId);
			if (component != null) break;
		}
		*/
		return component;
	}
	
	static public void DestroyComponent(Component pComponent) {
		pComponent.onDestroy();
	}
	
	static public Component CreateComponent(Class<? extends Component> pClass, GameObject pGameObject) {
		Component component = null;
		Assert.assertNotNull(pClass);
		
		try {
			Constructor<?> ctor = pClass.getConstructor(GameObject.class);
			component = (Component)ctor.newInstance(pGameObject);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		// Attach this component to a game object
		if (component.getInstanceType() == Component.skInstanceType_Single) {
			Component existingComponent = pGameObject.getComponentOfType(pClass);
			if (existingComponent != null) {
				pGameObject.detachComponent(existingComponent);
				component.inheritFrom(existingComponent);
				
				ComponentFactory.DestroyComponent(existingComponent);
			}
		}
		
		pGameObject.attachComponent(component);
		
		return component;
	}
	
	static public Component CreateComponent(String pType, GameObject pGameObject) {
		Class<? extends Component> c = smComponentTypes.get(pType.toLowerCase(Locale.ENGLISH));
		return CreateComponent(c, pGameObject);
	}
	
	static public Class<? extends Component> GetComponentClass(String pType) {
		Assert.assertTrue(smComponentTypes.containsKey(pType.toLowerCase(Locale.ENGLISH)));
		return smComponentTypes.get(pType.toLowerCase(Locale.ENGLISH));
	}
	
	static public Component LoadComponentFromXml(String pFilename, GameObject pGameObject) {
		Component component = null;
		
		FileHandle fh = Gdx.files.internal(pFilename);
		
		if (fh.exists()) {
			Document document = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				document = db.parse(fh.file());
			} catch (ParserConfigurationException e) {
			} catch (SAXException e) {
			} catch (IOException e) {
			}
			
			Element rootNode = document.getDocumentElement();
			component = LoadComponentInternal(rootNode, pGameObject);
		}
		
		Assert.assertNotNull(component);
		return component;
	}
	
	static public Component LoadComponentInternal(Element pXmlNode, GameObject pGameObject) {
		Component component = null;
		
		// If this is an include, load the component from Xml, otherwise
		// create the component.
		if (pXmlNode.getNodeName().equalsIgnoreCase("include")) {
			return LoadComponentFromXml(pXmlNode.getAttribute("file"), pGameObject);
		} else {
			component = CreateComponent(pXmlNode.getNodeName(), pGameObject);
			Assert.assertNotNull(component);
			
			// Initialise the component from Xml
			component.parseFromXml(pXmlNode);
			
			// Setup the component
			//if (!pGameObject.isLibraryGameObject()) {
			//	component.setup();
			//}
		}
		
		return component;
	}
}

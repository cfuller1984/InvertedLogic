package com.invertedlogic.scene;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class SceneFactory {
	public static Scene LoadSceneFromXml(String pFilename) {
		Scene scene = null;
		
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
			scene = LoadSceneInternal(rootNode);
			
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		
		return scene;
	}
	
	public static Scene LoadSceneInternal(Element pXmlNode) {
		Scene scene = new Scene();
		//Scene.SetCurrentScene(scene);
		
		scene.loadFromXml(pXmlNode);
		
		return scene;
	}
}

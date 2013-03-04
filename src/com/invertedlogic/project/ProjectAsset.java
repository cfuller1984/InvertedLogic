package com.invertedlogic.project;

import org.w3c.dom.Element;

public interface ProjectAsset {
	public void load();
	public void parseFromXml(Element pXmlNode);
}

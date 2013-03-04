package com.invertedlogic.assets;

public interface IAsset {
	void incRefCount();
	void decRefCount();
	int getRefCount();
	String getId();
	
	void dispose();
	
	boolean isLoaded();
}

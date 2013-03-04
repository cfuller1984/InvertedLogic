package com.invertedlogic.assets;

import com.invertedlogic.util.Assert;

public class Asset implements IAsset {
	private int mRefCount;
	private String mId;
	
	public Asset(String pId) {
		mId = pId;
	}
	
	@Override
	public void incRefCount() {
		mRefCount++;
	}

	@Override
	public void decRefCount() {
		Assert.assertTrue(mRefCount > 0);
		mRefCount--;
	}

	@Override
	public int getRefCount() {
		return mRefCount;
	}

	@Override
	public String getId() {
		return mId;
	}

	@Override
	public boolean isLoaded() {
		return false;
	}
	
	@Override
	public void dispose() {
		
	}
}

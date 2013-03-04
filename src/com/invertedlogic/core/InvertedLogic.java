package com.invertedlogic.core;

import com.invertedlogic.assets.AssetFactory;
import com.invertedlogic.assets.FontFactory;
import com.invertedlogic.assets.TextureFactory;
import com.invertedlogic.componentsystem.ComponentFactory;
import com.invertedlogic.graphics.LayerManager;
import com.invertedlogic.input.InputManager;
import com.invertedlogic.util.Assert;

public class InvertedLogic {
	static InvertedLogic smThis;
	public static void Create() { Assert.assertNull(smThis); smThis = new InvertedLogic(); }
	public static InvertedLogic Get() { return smThis; }
	
	public InvertedLogic() {
		
	}
	
	public void init() {
		// Create Factories
		AssetFactory.Create();
		TextureFactory.Create();
		FontFactory.Create();
		
		// Input processing
		InputManager.Create();
		
		// Layers
		LayerManager.Create();
		
		// Register built-in component types
		ComponentFactory.RegisterBuiltInComponentTypes();
	}
}

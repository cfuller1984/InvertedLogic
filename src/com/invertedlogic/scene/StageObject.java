package com.invertedlogic.scene;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.invertedlogic.gameobject.GameObject;

public class StageObject extends GameObject {
	Stage mStage;
	
	public StageObject(Stage pStage) {
		super(null);
		
		mStage = pStage;
		addToStage(mStage);
	}
	
	public Stage getStage() {
		return mStage;
	}
}

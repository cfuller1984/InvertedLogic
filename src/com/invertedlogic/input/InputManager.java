package com.invertedlogic.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public class InputManager {
	static InputManager smThis = null;
	public static void Create() { smThis = new InputManager(); }
	public static void Dispose() {
		smThis.dispose();
		smThis = null;
	}
	public static InputManager Get() { return smThis; }
	
	InputMultiplexer mInputMultiplexer;
	
	public InputManager() {
		mInputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(mInputMultiplexer);
	}
	
	void dispose() {
		mInputMultiplexer.clear();
	}
	
	public void addInputProcessor(InputProcessor pInputProcessor) {
		mInputMultiplexer.addProcessor(pInputProcessor);
	}
	
	public void addInputProcessor(int pIndex, InputProcessor pInputProcessor) {
		mInputMultiplexer.addProcessor(pIndex, pInputProcessor);
	}
	
	public void removeInputProcessor(InputProcessor pInputProcessor) {
		mInputMultiplexer.removeProcessor(pInputProcessor);
	}
}

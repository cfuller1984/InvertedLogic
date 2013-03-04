package com.invertedlogic.particlesystem;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.invertedlogic.assets.TextureAsset;
import com.invertedlogic.assets.TextureFactory;
import com.invertedlogic.componentsystem.Component;
import com.invertedlogic.gameobject.GameObject;

public class ParticleSystem extends Component {
	String mTextureId;
	TextureAsset mTextureAsset;
	//SpriteParticleSystem mParticleSystem;
	
	public ParticleSystem(GameObject pGameObject) {
		super(pGameObject, Component.skInstanceType_Single);
	}

	@Override
	protected void parseFromXml(Element pXmlNode) {
		NamedNodeMap attributes = pXmlNode.getAttributes();
		
		for (int i = 0; i < attributes.getLength(); ++i) {
			Node attribute = attributes.item(i);
			String name = attribute.getNodeName();
			
			if (name.equalsIgnoreCase("texture")) {
				mTextureId = attribute.getNodeValue();
			}
		}
	}
	
	@Override
	public void setup() {
		mTextureAsset = TextureFactory.requestTexture(mTextureId, TextureFilter.Linear);
		/*
		mParticleSystem = new SpriteParticleSystem(new PointParticleEmitter(0, 0), 6, 10, 200, mTextureAsset.getTexture(), EngineData.getEngine().getVertexBufferObjectManager());
		mParticleSystem.addParticleInitializer(new BlendFunctionParticleInitializer<Sprite>(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE));
		mParticleSystem.addParticleInitializer(new VelocityParticleInitializer<Sprite>(15, 22, -60, -90));
		mParticleSystem.addParticleInitializer(new AccelerationParticleInitializer<Sprite>(5, 15));
		mParticleSystem.addParticleInitializer(new RotationParticleInitializer<Sprite>(0.0f, 360.0f));
		mParticleSystem.addParticleInitializer(new ColorParticleInitializer<Sprite>(mMaterial.getColour().getColor()));
		mParticleSystem.addParticleInitializer(new ExpireParticleInitializer<Sprite>(11.5f));

		mParticleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(0, 5, 0.5f, 2.0f));
		mParticleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(2.5f, 3.5f, 1.0f, 0.0f));
		mParticleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(3.5f, 4.5f, 0.0f, 1.0f));
		//mParticleSystem.addParticleModifier(new ColorParticleModifier<Sprite>(0.0f, 11.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f));
		mParticleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(4.5f, 11.5f, 1.0f, 0.0f));
		
		mTransform.attachEntity(mParticleSystem);*/
	}

	@Override
	public void inheritFrom(Component pComponent) {
		ParticleSystem particleSystem = (ParticleSystem)pComponent;
		mTextureId = particleSystem.mTextureId;
	}

	@Override
	public void reset() {
	}

	@Override
	public void update() {
	}

	@Override
	public void onDestroy() {
		mTextureAsset = null;
		
		//mTransform.detachEntity(mParticleSystem);
		//mParticleSystem = null;
	}

}

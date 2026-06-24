package com.honor.blitzremake.entity;

import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.graphics.VertexArray;

public class TexturedModel {

	private VertexArray mesh;
	private Texture texture;
	
	public boolean hasTransparency = false;

	public TexturedModel(VertexArray mesh, Texture texture) {
		this.mesh = mesh;
		this.texture = texture;
	}

	public VertexArray getMesh() {
		return mesh;
	}

	public Texture getTexture() {
		return texture;
	}

}

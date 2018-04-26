package com.honor.blitzremake.entity.others;

import java.awt.Rectangle;

import com.honor.blitzremake.Resources;
import com.honor.blitzremake.entity.Entity;
import com.honor.blitzremake.entity.TexturedModel;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;

public class Collider extends Entity {

	public Collider(float x, float y, int width, int height) {
		super(x, y);
		w = width;
		h = height;
		model = new TexturedModel(Resources.debug_mesh, Texture.debug);
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (position.x - w / 2), (int) (position.y - h / 2), (int) w, (int) h);
	}

	@Override
	public void update() {
	}
	// Only for debug purposes
	public void render() {
		Texture.debug.bind();
		Shader.BASIC.setUniformMatrix("ml_matrix", getModelMatrix());
		Resources.debug_mesh.render(Shader.LEVEL);
		;
		Texture.debug.unbind();
	}

	public Matrix4f getModelMatrix() {
		return Matrix4f.translate(new Vector3f(position.x, position.y, 0.1f)).multiply(Matrix4f.scale(new Vector3f(w * 0.1f, h * 0.1f, 1f)));
	}

	public void setWH(float w, float h) {
		this.w = w;
		this.h = h;
	}
	
}

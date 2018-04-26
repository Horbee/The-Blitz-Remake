package com.honor.blitzremake.entity.others;

import java.awt.Rectangle;

import static org.lwjgl.opengl.GL11.*;
import com.honor.blitzremake.Resources;
import com.honor.blitzremake.camera.Camera;
import com.honor.blitzremake.entity.Entity;
import com.honor.blitzremake.entity.TexturedModel;
import com.honor.blitzremake.entity.particle.SmokeFXEmitter;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;

public class SmokeGrid extends Entity {

	private SmokeFXEmitter smokefx_emitter;
	
	public SmokeGrid(float x, float y) {
		super(x, y);
		model = new TexturedModel(Resources.decal3_mesh, Texture.decal3);
		model.hasTransparency = true;
		w = 88.0f;
		h = 32.0f;
		
		smokefx_emitter = new SmokeFXEmitter(x, y);
	}

	@Override
	public void update() {

	}
	
	@Override
	public void render() {
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		Shader.LEVEL.enable();
		Shader.LEVEL.setUniformMatrix("vw_matrix", Camera.getViewMatrix());
		Shader.LEVEL.setUniformMatrix("ml_matrix", getModelMatrix());
		model.getTexture().bind();
		model.getMesh().render(Shader.LEVEL);
		model.getTexture().unbind();

		Shader.LEVEL.disable();
		glDisable(GL_BLEND);

	}

	public Rectangle getBounds() {
		return new Rectangle((int) position.x, (int) position.y, (int) w, (int) h);
	}

	public Matrix4f getModelMatrix() {
		return Matrix4f.translate(position).multiply(Matrix4f.rotateAngle(90)).multiply(Matrix4f.scale(new Vector3f(1.6f, 1.5f, 1f)));
	}
	
	public SmokeFXEmitter getEmitter() {
		return smokefx_emitter;
	}
}

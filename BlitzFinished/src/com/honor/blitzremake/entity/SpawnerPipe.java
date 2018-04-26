package com.honor.blitzremake.entity;

import com.honor.blitzremake.Resources;
import com.honor.blitzremake.entity.particle.GooEmitter;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;

public class SpawnerPipe extends Entity {

	private Vector3f pos = new Vector3f();
	private int angle;
	private GooEmitter goo_emitter;

	public SpawnerPipe(float x, float y, int angle) {
		pos.set(x, y, 0);
		this.angle = angle;
		goo_emitter = new GooEmitter(x, y, angle);
		model = new TexturedModel(Resources.pipe_mesh, Texture.pipe);
	}

	public void update() {
		goo_emitter.update();
	}

	public void renderGoo() {
		goo_emitter.renderParticles();
	}

	public Matrix4f getModelMatrix() {
		return Matrix4f.translate(new Vector3f(pos.x, pos.y, 0)).multiply(Matrix4f.rotateAngle(angle));
	}
}

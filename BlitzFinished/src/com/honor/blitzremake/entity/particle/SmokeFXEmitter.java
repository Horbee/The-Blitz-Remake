package com.honor.blitzremake.entity.particle;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.honor.blitzremake.Resources;
import com.honor.blitzremake.camera.Camera;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;

public class SmokeFXEmitter {

	public static List<SmokeFXEmitter> smokeFXEmitters = new ArrayList<SmokeFXEmitter>();

	private Random rnd = new Random();
	private Vector3f pos = new Vector3f();
	private List<SmokeParticle> particles = new ArrayList<SmokeParticle>();
	public boolean shouldRemove = false;

	public SmokeFXEmitter(float x, float y) {
		pos.x = x;
		pos.y = y;

		smokeFXEmitters.add(this);
	}

	public static void updateEmitters() {
		for (int i = 0; i < smokeFXEmitters.size(); i++) {
			smokeFXEmitters.get(i).update();
		}
	}

	private void update() {

		for (int i = 0; i < 10; i++) {
			SmokeParticle p = new SmokeParticle(pos.x, pos.y, 1, true);
			p.moves = true;
			p.rot = (float) Math.toRadians(rnd.nextInt(360));
			p.grow = true;
			p.maxScale = 15.0f;
			p.growAmount = 0.08f;
			particles.add(p);
		}

		for (int i = 0; i < particles.size(); i++) {
			if (particles.get(i).isRemoved) {
				particles.remove(i);
			} else {
				particles.get(i).update();
			}
		}

	}

	public static void renderFX() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		Shader.PARTICLE.enable();
		Shader.PARTICLE.setUniformMatrix("vw_matrix", Camera.getViewMatrix());

		Resources.electric_mesh.bind(Shader.PARTICLE);
		for (int i = 0; i < smokeFXEmitters.size(); i++) {
			for (int j = 0; j < smokeFXEmitters.get(i).particles.size(); j++) {
				SmokeParticle p = smokeFXEmitters.get(i).particles.get(j);
				p.texture.bind();
				Shader.PARTICLE.setUniform3f("scale", new Vector3f(p.scale, p.scale, 1.0f));
				// System.out.println(p.scale);
				Shader.PARTICLE.setUniformMatrix("ml_matrix", Matrix4f.translate(p.position).multiply(Matrix4f.rotateRad(p.rot += 0.001f)));
				Shader.PARTICLE.setUniform1f("life", p.life);
				Shader.PARTICLE.setUniform1f("time", p.time);
				Shader.PARTICLE.setUniform1f("praticleType", 3);

				Resources.electric_mesh.draw();
				p.texture.unbind();
			}
		}

		Shader.PARTICLE.disable();
		glDisable(GL_BLEND);
	}
}

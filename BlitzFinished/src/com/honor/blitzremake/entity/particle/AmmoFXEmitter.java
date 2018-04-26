package com.honor.blitzremake.entity.particle;

import static org.lwjgl.opengl.GL11.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.honor.blitzremake.Resources;
import com.honor.blitzremake.camera.Camera;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;

public class AmmoFXEmitter {

	public static List<AmmoFXEmitter> ammoFXEmitters = new ArrayList<AmmoFXEmitter>();

	private Random rnd = new Random();
	private Vector3f pos = new Vector3f();
	private List<Particle> particles = new ArrayList<Particle>();
	private List<Particle> electricity = new ArrayList<Particle>();
	public boolean spawnLightning = true;
	public boolean shouldRemove = false;

	public AmmoFXEmitter(float x, float y) {
		pos.x = x;
		pos.y = y;

		ammoFXEmitters.add(this);
	}

	public static void updateEmitters() {
		for (int i = 0; i < ammoFXEmitters.size(); i++) {
			ammoFXEmitters.get(i).update();
		}
	}

	private void update() {

		if (particles.size() < 20) {
			Particle p = new Particle(pos.x, pos.y, 50, false);
			particles.add(p);
		}

		if (spawnLightning) {
			for (int i = 0; i < 2; i++) {
				Particle p = new Particle(pos.x, pos.y, 1, true);
				p.moves = false;
				p.rot = (float) Math.toRadians(rnd.nextInt(360));
				p.grow = true;
				p.maxScale = 2.0f;
				p.growAmount = 0.02f;
				electricity.add(p);
			}
		}

		for (int i = 0; i < particles.size(); i++) {
			if (particles.get(i).isRemoved) {
				particles.remove(i);
			} else {
				particles.get(i).update();
			}
		}

		for (int i = 0; i < electricity.size(); i++) {
			if (electricity.get(i).isRemoved) {
				electricity.remove(i);
			} else {
				electricity.get(i).update();
			}
		}

	}

	public static void renderFX() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		Shader.PARTICLE.enable();
		Shader.PARTICLE.setUniformMatrix("vw_matrix", Camera.getViewMatrix());

		Resources.particle_mesh.bindParticle(Shader.PARTICLE);
		for (int i = 0; i < ammoFXEmitters.size(); i++) {
			for (int j = 0; j < ammoFXEmitters.get(i).particles.size(); j++) {
				Particle p = ammoFXEmitters.get(i).particles.get(j);
				Shader.PARTICLE.setUniformMatrix("ml_matrix", Matrix4f.translate(p.position));
				Shader.PARTICLE.setUniform1f("life", p.life);
				Shader.PARTICLE.setUniform1f("time", p.time);
				Shader.PARTICLE.setUniform1f("praticleType", 0);
				Shader.PARTICLE.setUniform2f("particlePosition", p.position.x, p.position.y);
				Resources.particle_mesh.draw();
			}
		}

		Resources.electric_mesh.bind(Shader.PARTICLE);
		for (int i = 0; i < ammoFXEmitters.size(); i++) {
			for (int j = 0; j < ammoFXEmitters.get(i).electricity.size(); j++) {
				Particle p = ammoFXEmitters.get(i).electricity.get(j);
				p.texture.bind();
				Shader.PARTICLE.setUniform3f("scale", new Vector3f(p.scale, p.scale, 1.0f));
				// System.out.println(p.scale);
				Shader.PARTICLE.setUniformMatrix("ml_matrix", Matrix4f.translate(p.position).multiply(Matrix4f.rotateRad(p.rot += 0.001f)));
				Shader.PARTICLE.setUniform1f("life", p.life);
				Shader.PARTICLE.setUniform1f("time", p.time);
				Shader.PARTICLE.setUniform1f("praticleType", 1);

				Resources.electric_mesh.draw();
				p.texture.unbind();
			}
		}

		Shader.PARTICLE.disable();
		glDisable(GL_BLEND);
	}

}

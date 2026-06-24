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
import com.honor.blitzremake.util.Util;

public class BloodEmitter {

	public List<BloodParticle> particles = new ArrayList<BloodParticle>();
	public Vector3f pos = new Vector3f();
	public boolean shouldRemove = false;
	private static Random rnd = new Random();
	private Vector3f color = new Vector3f();
	private int time;
	private boolean firstPhase = true;

	public BloodEmitter(float x, float y, Vector3f bloodColor) {
		pos.set(x, y, 0);
		color = bloodColor;
	}

	public void update() {
		for (int i = 0; i < particles.size(); i++) {
			if (particles.get(i).isRemoved()) {
				particles.remove(i);
			} else {
				particles.get(i).update();
			}
		}

		if (firstPhase) {
			if (time % 10 == 0) {
				if (particles.size() < 4) {
					int angle = rnd.nextInt(360);
					float dx = rnd.nextInt(10);
					float dy = rnd.nextInt(10);
					particles.add(new BloodParticle(pos.x + dx, pos.y + dy, angle));
				}
			}
			if (particles.size() == 4) firstPhase = false;
		}
		
		
		if (particles.size() == 0) remove();

		time++;
	}

	public void renderParticles() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		Shader.BLOODSPLAT.enable();
		Shader.BLOODSPLAT.setUniformMatrix("vw_matrix", Camera.getViewMatrix());
		Shader.BLOODSPLAT.setUniform3f("particleColor", color);

		Resources.blood_particle_mesh.bind(Shader.BLOODSPLAT);
		for (int i = 0; i < particles.size(); i++) {
			BloodParticle p = particles.get(i);

			Shader.BLOODSPLAT.setUniform1f("blendFactor", (float) (p.blendFactor * 0.005));

			Vector3f scale = new Vector3f((float) (p.time * 0.03) + 0.2f, (float) (p.time * 0.03) + 0.2f, 1.0f);
			scale.x = Util.Clamp(scale.x, 0.0f, 1.0f);
			scale.y = Util.Clamp(scale.y, 0.0f, 1.0f);
			Shader.BLOODSPLAT.setUniformMatrix("ml_matrix", Matrix4f.translate(p.pos).multiply(Matrix4f.rotateAngle(p.angle)).multiply(Matrix4f.scale(scale)));

			p.texture.bind();
			Resources.blood_particle_mesh.draw();
			p.texture.unbind();
		}

		Shader.BLOODSPLAT.disable();
		glDisable(GL_BLEND);
		//System.out.println(particles.size());
	}

	private void remove() {
		shouldRemove = true;
	}
}

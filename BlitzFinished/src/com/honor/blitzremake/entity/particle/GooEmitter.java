package com.honor.blitzremake.entity.particle;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;

import com.honor.blitzremake.Resources;
import com.honor.blitzremake.camera.Camera;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;
import com.honor.blitzremake.util.Util;

public class GooEmitter {

	public List<GooParticle> particles = new ArrayList<GooParticle>();
	public Vector3f pos = new Vector3f();
	private int time;
	private int angle;
	
	public GooEmitter(float x, float y, int angle) {
		float dx = (float) (Math.cos(Math.toRadians(angle)) * (30) - Math.sin(Math.toRadians(angle)) * (-1.0));
		float dy = (float) (Math.sin(Math.toRadians(angle)) * (30) + Math.cos(Math.toRadians(angle)) * (-1.0));
		
		pos.set(x + dx * 2, y + dy * 2, 0);
		this.angle = angle;
	}
	
	public void update() {
		for (int i = 0; i < particles.size(); i++) {
			if (particles.get(i).isRemoved()) {
				particles.remove(i);
			} else {
				particles.get(i).update();
			}
		}

		if (particles.size() < 1) {
			
		}
		if (time % 2 == 0) {
			particles.add(new GooParticle(pos.x, pos.y, angle));
		}
		time++;
	}
	
	public void renderParticles() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		Shader.BLOODSPLAT.enable();
		Shader.BLOODSPLAT.setUniformMatrix("vw_matrix", Camera.getViewMatrix());
		Shader.BLOODSPLAT.setUniform3f("particleColor", GooParticle.color);
		Texture.bloodSplat1.bind();
		Resources.blood_particle_mesh.bind(Shader.BLOODSPLAT);
		for (int i = 0; i < particles.size(); i++) {
			GooParticle p = particles.get(i);
			Shader.BLOODSPLAT.setUniform1f("blendFactor", (float) (p.blendFactor * 0.01));
			Vector3f scale;
			if (p.growth) {
				scale = new Vector3f(0.8f, (float) (p.growth_time * 0.02) + 0.2f, 1.0f);
			} else {
				scale = new Vector3f(0.8f, 0.2f, 1.0f);
			}
			scale.x = Util.Clamp(scale.x, 0.0f, 1.0f);
			scale.y = Util.Clamp(scale.y, 0.0f, 1.0f);
			Shader.BLOODSPLAT.setUniformMatrix("ml_matrix", Matrix4f.translate(p.pos).multiply(Matrix4f.rotateAngle(angle)).multiply(Matrix4f.scale(scale)));
			Resources.blood_particle_mesh.draw();
		}

		Texture.bloodSplat1.unbind();
		Shader.BLOODSPLAT.disable();
		glDisable(GL_BLEND);
		//System.out.println(particles.size());
	}
}

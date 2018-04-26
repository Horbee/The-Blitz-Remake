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

public class SparkleEmitter {
	public List<SparkleParticle> particles = new ArrayList<SparkleParticle>();
	public Vector3f pos = new Vector3f();
	public boolean shouldRemove = false;
	
	public SparkleEmitter(float x, float y) {
		pos.set(x, y, 0);
		for (int i = 0; i < 10; i++) {
			particles.add(new SparkleParticle(pos.x, pos.y));			
			
		}
	}
	
	public void update() {
		for (int i = 0; i < particles.size(); i++) {
			if (particles.get(i).isRemoved()) {
				particles.remove(i);
			} else {
				particles.get(i).update();
			}
		}

		if (particles.size() ==  0) {
			shouldRemove = true;
		}

	}
	
	public void renderParticles() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);
		
		Shader.SPARKLE.enable();
		Texture.sparkle.bind();
		Shader.SPARKLE.setUniformMatrix("vw_matrix", Camera.getViewMatrix());
		Shader.SPARKLE.setUniform3f("particleColor", new Vector3f(0.9f, 0.1f, 0.1f));
		
		Resources.sparkle_mesh.bind(Shader.SPARKLE);

		for (int i = 0; i < particles.size(); i++) {
			SparkleParticle sp = particles.get(i);
			Shader.SPARKLE.setUniform1f("intensity", 5.0f - sp.blendFactor * 0.08f);
			Shader.SPARKLE.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(sp.pos.x, sp.pos.y, 0)).multiply(Matrix4f.rotateAngle(sp.angle)));
			float dx = (float) (Math.cos(Math.toRadians(sp.angle)) * (45) - Math.sin(Math.toRadians(sp.angle)) * (-1.0));
			float dy = (float) (Math.sin(Math.toRadians(sp.angle)) * (45) + Math.cos(Math.toRadians(sp.angle)) * (-1.0));
			Shader.SPARKLE.setUniform2f("particlePosition", sp.pos.x - dx, sp.pos.y - dy);
			Resources.sparkle_mesh.draw();
			
		}
		
		Texture.sparkle.unbind();
		Shader.SPARKLE.disable();
		glDisable(GL_BLEND);
	}

}

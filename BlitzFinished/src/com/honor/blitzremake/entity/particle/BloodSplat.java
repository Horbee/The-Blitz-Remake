package com.honor.blitzremake.entity.particle;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.honor.blitzremake.Resources;
import com.honor.blitzremake.camera.Camera;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;
import com.honor.blitzremake.util.Util;

public class BloodSplat {

	public static List<BloodSplat> bloodsplats = new ArrayList<BloodSplat>();

	public Vector3f pos = new Vector3f();
	public Vector3f scale = new Vector3f();
	public float blendFactor = 50.0f;
	public float time, growth_time;
	public float angle;

	private float xa, ya;
	private static Random rnd = new Random();
	private boolean removed = false;

	public BloodSplat(float x, float y, float angle) {
		pos.set(x, y, 0);
		this.angle = (float) (angle - Math.toRadians(90 + rnd.nextInt(30) - 20));
		xa = (float) (5 * Math.cos(Math.toRadians(-angle)) + (5 * (rnd.nextDouble() - 0.5)));
		ya = (float) (5 * Math.sin(Math.toRadians(-angle)) + (5 * (rnd.nextDouble() - 0.5)));
	}

	public void update() {
		xa *= 0.5;
		ya *= 0.5;
		pos.x -= xa;
		pos.y -= ya;

		if (time > 360) {
			blendFactor++;
			// time = 0;
		}

		if (blendFactor * 0.005 >= 1) {
			removed = true;
		}
		
		scale.set((time * 0.02f), (time * 0.05f), 1.0f);
		scale.x = Util.Clamp(scale.x, 0.0f, 0.3f);
		scale.y = Util.Clamp(scale.y, 0.0f, 0.8f);
				
		
		time++;
	}

	public static void updateBlood() {
		for (int i = 0; i < bloodsplats.size(); i++) {
			if (bloodsplats.get(i).isRemoved()) {
				bloodsplats.remove(i);
			} else {
				bloodsplats.get(i).update();
			}
		}
	}

	public static void renderBlood() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		Shader.BLOODSPLAT.enable();
		Shader.BLOODSPLAT.setUniformMatrix("vw_matrix", Camera.getViewMatrix());
		Shader.BLOODSPLAT.setUniform3f("particleColor", new Vector3f(0.0f, 0.8f, 0.0f));

		Resources.bloodSplat_mesh.bind(Shader.BLOODSPLAT);

		Texture.blood2.bind();

		for (int i = 0; i < bloodsplats.size(); i++) {
			BloodSplat bs = bloodsplats.get(i);
			Shader.BLOODSPLAT.setUniform1f("blendFactor", (float) (bs.blendFactor * 0.005));
			Shader.BLOODSPLAT.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(bs.pos.x, bs.pos.y, 0)).multiply(Matrix4f.rotateRad(bs.angle)).multiply(Matrix4f.scale(bs.scale)));
			Resources.bloodSplat_mesh.draw();
		}

		Texture.blood2.unbind();
		Shader.BLOODSPLAT.disable();
		glDisable(GL_BLEND);
	}

	public boolean isRemoved() {
		return removed;
	}
}

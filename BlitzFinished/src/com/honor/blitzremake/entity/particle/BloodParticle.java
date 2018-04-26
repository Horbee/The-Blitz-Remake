package com.honor.blitzremake.entity.particle;

import java.util.Random;

import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.math.Vector3f;

public class BloodParticle {
	
	public Vector3f pos = new Vector3f();
	public float blendFactor = 50.0f;
	public float time, growth_time;
	public Texture texture;
	public int angle;

	private static Random rnd = new Random();
	private boolean removed = false;

	public BloodParticle(float x, float y, int angle) {
		pos.set(x, y, 0);
		this.angle = angle;
		int cube = rnd.nextInt(3);
		if (cube == 0) {
			texture = Texture.blood1;
		}else if(cube == 1){
			texture = Texture.blood2;
		}else if (cube == 2){
			texture = Texture.blood3;
		}
	}

	public void update() {

		if (time > 360) {
			blendFactor++;
		}

		if (blendFactor * 0.005 >= 1) {
			removed = true;
		}


		time++;
	}

	public boolean isRemoved() {
		return removed;
	}
}

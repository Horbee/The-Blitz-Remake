package com.honor.blitzremake.entity.particle;

import java.util.Random;

import com.honor.blitzremake.math.Vector3f;

public class GooParticle {

	public Vector3f pos = new Vector3f();
	public static Vector3f color = new Vector3f();
	public float blendFactor = 10.0f;
	public float time, growth_time;
	public boolean growth = false;

	private float xa, ya;
	private static Random rnd = new Random();
	private boolean removed = false;

	public GooParticle(float x, float y, int angle) {
		pos.set(x, y, 0);
		color.set(0.0f, 0.8f, 0.0f);

		//life = 100 + rnd.nextInt(100);

		xa = (float) (5 * Math.cos(Math.toRadians(angle)) + (5 * (rnd.nextDouble() - 0.5)));
		ya = (float) (5 * Math.sin(Math.toRadians(angle)) + (5 * (rnd.nextDouble() - 0.5)));
		//System.out.println(Math.toRadians(angle));
		// xa = (float) (12 * (rnd.nextDouble()));
		// ya = (float) (speed * (rnd.nextFloat() - 0.8));

	}

	public void update() {
		xa *= 0.95;
		ya *= 0.95;
		pos.x += xa;
		pos.y += ya;

		if (time > 20) {
			growth = true;
			// time = 0;
		}
		if (growth) {
			growth_time++;
		}

		if (blendFactor * 0.01 >= 1) {
			removed = true;
		}

		blendFactor++;

		time++;
	}

	public boolean isRemoved() {
		return removed;
	}

}

package com.honor.blitzremake.entity.particle;

import java.util.Random;
import com.honor.blitzremake.math.Vector3f;

public class SparkleParticle {

	public Vector3f pos = new Vector3f();
	public static Vector3f color = new Vector3f();
	public float blendFactor = 50.0f;
	public float time, growth_time;
	public boolean growth = false;
	public float angle;
	private float speed;

	private float xa, ya;
	private static Random rnd = new Random();
	private boolean removed = false;

	public SparkleParticle(float x, float y) {
		pos.set(x, y, 0);
		angle = rnd.nextInt(360);
		speed = rnd.nextInt(5) + 3;
		color.set(0.0f, 0.8f, 0.0f);
		xa = (float) (speed * (Math.cos(Math.toRadians(angle))));
		ya = (float) (speed * (Math.sin(Math.toRadians(angle))));
	}

	public void update() {
		pos.x -= xa;
		pos.y -= ya;

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

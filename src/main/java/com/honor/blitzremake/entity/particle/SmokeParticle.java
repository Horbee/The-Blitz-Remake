package com.honor.blitzremake.entity.particle;

import java.util.Random;

import com.honor.blitzremake.entity.Entity;
import com.honor.blitzremake.graphics.Texture;

public class SmokeParticle extends Entity {

	public float time;
	public float life;
	private float speed = 1.0f;
	private float xa, ya;
	public float rot;
	public boolean grow = false;
	public float growAmount;
	public float maxScale;
	public float scale = 1.0f;
	public boolean moves = true;
	public Texture texture;

	private static Random rnd = new Random();

	public SmokeParticle(float x, float y, float life, boolean img) {
		super(x + rnd.nextInt(20), y + rnd.nextInt(30));
		if (img)
			position.z = rnd.nextFloat();

		this.life = life + rnd.nextInt(30);

		speed = rnd.nextFloat() + 1.5f;
		this.xa = (rnd.nextFloat() - 0.5f) * speed;
		this.ya = (rnd.nextFloat() - 0.5f) * speed;

		if (img) {
			int tex = rnd.nextInt(4);
			if (tex == 0)
				texture = Texture.smoke1;
			if (tex == 1)
				texture = Texture.smoke2;
			if (tex == 2)
				texture = Texture.smoke3;
			if (tex == 3)
				texture = Texture.smoke4;
		}
	}

	public void update() {
		time += 0.5f;
		if (time > 7500)
			time = 0;
		if (time >= life) {
			remove();
		}

		if (grow) {
			if (scale < maxScale) {
				scale += growAmount;
			}
		}

		if (moves) {
			position.x += xa;
			position.y += ya;
		}
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

}

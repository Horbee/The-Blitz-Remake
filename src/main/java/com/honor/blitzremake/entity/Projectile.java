package com.honor.blitzremake.entity;

import java.awt.Rectangle;
import java.util.Random;
import com.honor.blitzremake.Resources;
import com.honor.blitzremake.entity.others.Collider;
import com.honor.blitzremake.entity.particle.BloodSplat;
import com.honor.blitzremake.entity.particle.SparkleEmitter;
import com.honor.blitzremake.graphics.Light;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.level.Wall;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.sound.Sound;

public class Projectile extends Mob {

	private float nx, ny;
	private float angle, range, speed;
	private float xOrigin, yOrigin;
	private static Random rnd = new Random();

	public Light light;

	public Projectile(float x, float y, float angle) {
		super(x, y);
		model = new TexturedModel(Resources.projectile_mesh, Texture.blast);
		w = 16.0f;
		h = 40.0f;
		this.angle = angle;
		xOrigin = x;
		yOrigin = y;
		range = 500.0f;
		speed = 10.0f;
		nx = (float) (speed * Math.cos(angle));
		ny = (float) (speed * Math.sin(angle));

		light = new Light(x, y, 0xffff0000, 50.0f);
		light.fadeOut = true;
	}

	private float distance() {
		float dx = xOrigin - position.x;
		float dy = yOrigin - position.y;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	private void move() {
		position.x += nx;
		position.y += ny;

		if (distance() > range) remove();
	}

	public void update() {
		move();
		collision();
		light.move(nx, ny);
		light.update();
	}

	private void collision() {
		if(getBounds().intersects(level.g.getBounds())) {
			level.g.health--;
			die();
			return;
		}
		
		for (int i = 0; i < level.colliders.size(); i++) {
			Collider c = level.colliders.get(i);
			if (getBounds().intersects(c.getBounds())) {
				die();
				return;
			}
		}
		
		for (int i = 0; i < level.walls.size(); i++) {
			Wall w = level.walls.get(i);
			if (getBounds().intersects(w.getBounds())) {
				die();
				return;
			}
		}

		// CHECK BOUNDING WALLS
		if (getBounds().intersects(new Rectangle(-32, 0, 32, 256 * 5))) {
			die();
			return;
		}

		if (getBounds().intersects(new Rectangle(256 * 5, 0, 32, 256 * 5))) {
			die();
			return;
		}

		if (getBounds().intersects(new Rectangle(0, 256 * 5, 256 * 5, 32))) {
			die();
			return;
		}

		if (getBounds().intersects(new Rectangle(0, -40, 265 * 5, 32))) {
			die();
			return;
		}

		for (int i = 0; i < level.enemys.size(); i++) {
			Enemy enemy = level.enemys.get(i);
			if (getBounds().intersects(enemy.getBounds())) {
				enemy.health--;
				SparkleEmitter emitter = new SparkleEmitter(position.x, position.y);
				level.sparkle_emitters.add(emitter);
				BloodSplat.bloodsplats.add(new BloodSplat(position.x, position.y, angle));

				int rndSound = rnd.nextInt(2);
				if (rndSound == 0) Sound.bugHit1.playAsSoundEffect(1.0f, 1.0f, false);
				else Sound.bugHit2.playAsSoundEffect(1.0f, 1.0f, false);

				remove();
				return;
			}
		}
	}

	private void die() {
		SparkleEmitter emitter = new SparkleEmitter(position.x, position.y);
		level.sparkle_emitters.add(emitter);
		Sound.hitWall.playAsSoundEffect(1.0f, 1.0f, false);
		remove();
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (position.x - w / 2), (int) (position.y - h / 2), (int) w, (int) h);
	}

	
	public Matrix4f getModelMatrix() {
		return Matrix4f.translate(position).multiply(Matrix4f.rotateRad((float) (angle - Math.toRadians(90))));
	}
	
	public Light getLight() {
		return light;
	}

}

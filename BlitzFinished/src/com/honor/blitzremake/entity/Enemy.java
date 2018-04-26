package com.honor.blitzremake.entity;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Rectangle;
import java.util.Random;

import com.honor.blitzremake.Resources;
import com.honor.blitzremake.camera.Camera;
import com.honor.blitzremake.entity.particle.BloodEmitter;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;
import com.honor.blitzremake.sound.Sound;

public class Enemy extends Mob {

	private float angle;
	private Random rnd = new Random();
	public float xa, ya;
	private int time;
	private float speed = 2.0f;
	public int health = 5;
	private float scale = 1.0f;

	public int max_distance = 250;
	
	public Enemy(float x, float y) {
		super(x, y);
		position.z = 0.3f;
		w = 64.0f;
		h = 64.0f;
		model = new TexturedModel(Resources.enemy_mesh, Texture.enemy);
		model.hasTransparency = true;
	}

	public void update() {

		double dx = level.player.position.x - position.x;
		double dy = level.player.position.y - position.y;
		double distance = Math.sqrt((dx * dx) + (dy * dy));
		if (distance < max_distance) {
			if (position.x > level.player.position.x) xa = -1.5f;
			if (position.x < level.player.position.x) xa = 1.5f;
			if (position.y > level.player.position.y) ya = -1.5f;
			if (position.y < level.player.position.y) ya = 1.5f;
			float dir = (float) Math.atan2(dy, dx);
			angle = (float) Math.toDegrees(dir) - 90;
		} else {
			moveRandomly(time);
		}

		if (xa != 0.0f || ya != 0.0f) {
			if (!collision(xa, 0)) {
				move(xa, 0);
			}
			if (!collision(0, ya)) {
				move(0, ya);
			}
		}

		time++;

		if (time % 480 == 0) {
			if (scale < 3) {
				scale += 0.5;
				health += 3;
			}
		}

		if (health <= 0) {
			die();
		}

	}

	private void moveRandomly(int time) {
		if (time % (rnd.nextInt(40) + 30) == 0) {
			float randomAngle = rnd.nextInt(360);
			xa = (float) (speed * Math.cos(Math.toRadians(randomAngle)));
			ya = (float) (speed * Math.sin(Math.toRadians(randomAngle)));
			angle = (float) (randomAngle - 90);
			if (rnd.nextInt(3) == 0) {
				xa = 0;
				ya = 0;
			}
		}
	}

	private void die() {
		remove();
		level.blood_emitters.add(new BloodEmitter(position.x, position.y, new Vector3f(0.0f, 0.8f, 0.0f)));
		Sound.bugDie.playAsSoundEffect(1.0f, 1.0f, false);
	}

	public void render() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		Shader.BASIC.enable();
		Shader.BASIC.setUniformMatrix("vw_matrix", Camera.getViewMatrix());
		Shader.BASIC.setUniformMatrix("ml_matrix", Matrix4f.translate(position).multiply(Matrix4f.rotateAngle(angle)).multiply(Matrix4f.scale(new Vector3f(scale, scale, 1.0f))));

		Texture.enemy.bind();
		Resources.enemy_mesh.render(Shader.BASIC);
		Texture.enemy.unbind();

		Shader.BASIC.disable();
		glDisable(GL_BLEND);

	}

	public Rectangle getBounds() {
		return new Rectangle((int) (position.x - w / 2), (int) (position.y - h / 2), (int) w, (int) h);
	}

	public Matrix4f getModelMatrix() {
		return Matrix4f.translate(position).multiply(Matrix4f.rotateAngle(angle)).multiply(Matrix4f.scale(new Vector3f(scale, scale, 1.0f)));
	}
}

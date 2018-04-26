package com.honor.blitzremake.entity;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.honor.blitzremake.Game;
import com.honor.blitzremake.camera.Camera;
import com.honor.blitzremake.graphics.Animation;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.graphics.VertexArray;
import com.honor.blitzremake.hud.HUD;
import com.honor.blitzremake.input.Input;
import com.honor.blitzremake.level.Level;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.sound.Sound;
import com.honor.blitzremake.util.Util;

public class Player extends Mob {

	private Texture top, topIdle, fire1, fire2;
	private Texture[] botRun;
	private Animation runAnim;
	private VertexArray mesh;
	private int fireCount, fireRate = 5;
	public static int ammo = 0;
	public static int health = 10;
	public static int pickedUpPowerCells = 0;
	private float rot;
	private int time;
	private float xxa, yya;
	public static boolean failed = false;
	public static boolean hasWeapon = false;

	private boolean runOnce;

	public Player(float x, float y) {
		super(x, y);
		SIZE = 64.0f;
		w = SIZE;
		h = SIZE;

		topIdle = new Texture("res/img/player/idle.png");
		top = topIdle;
		fire1 = new Texture("res/img/player/fire1.png");
		fire2 = new Texture("res/img/player/fire2.png");

		botRun = new Texture[8];
		for (int i = 0; i < botRun.length; i++) {
			botRun[i] = new Texture("res/img/player/botRun" + i + ".png");
		}
		runAnim = new Animation(botRun);

		mesh = new VertexArray(SIZE, SIZE, 0.1f);
	}

	public float angleMouse() {
		// MUST TWEEK!! in opengl 0,0 is in the bottom left corner
		int mx = Mouse.getX();
		int my = Display.getDisplayMode().getHeight() - Mouse.getY();

		float dx = (Game.WIDTH / 2) - mx;
		float dy = (Game.HEIGHT / 2) - my;

		// float dx = (position.x) - mx;
		// float dy = (position.y) - my;

		rot = (float) Math.atan2(dy, dx);
		return (float) (rot - Math.toRadians(270));
	}

	public void update() {
		int xa = 0;
		int ya = 0;

		if (xxa != 0)
			xxa -= Util.abs(xxa);
		if (yya != 0)
			yya -= Util.abs(yya);

		xa += xxa;
		ya += yya;

		if (Input.up)
			ya -= 3.0f;
		if (Input.down)
			ya += 3.0f;
		if (Input.left)
			xa -= 3.0f;
		if (Input.right)
			xa += 3.0f;

		if (xa != 0.0f || ya != 0.0f) {
			if (!collision(xa, 0)) {
				move(xa, 0);
			}
			if (!collision(0, ya)) {
				move(0, ya);
			}
			runAnim.update();

			if (!Sound.walk1.isPlaying())
				Sound.walk1.playAsSoundEffect(1.0f, 1.0f, false);
			else if (!Sound.walk2.isPlaying())
				Sound.walk2.playAsSoundEffect(1.0f, 1.0f, false);

		} else {
			runAnim.setFrame(0);
		}

		if (health < 10) {
			if (time % 120 == 0) {
				health++;
			}
		}
		if (enemyCollision()) {
			health--;
			health = (int) Util.Clamp(health, 0, 10);
			if (health == 0) {
				failed = true;
				level.end();
			}
		}
		if (pickedUpPowerCells == 8) {
			if (!runOnce) {
				runOnce = true;
				level.setEnemysAggro();
				HUD.resetTime();
				Level.phase = 3;
			}
			if (inElevator()) {
				level.end();
			}
		}
		HUD.health = health;

		top = topIdle;
//		if (Mouse.isButtonDown(0) || Input.controller.getAxisValue(4) > 0f) {
		if (Mouse.isButtonDown(0)) {
			shoot();
		}

		fireRate--;
		level.setOffset((int) (position.x - Game.WIDTH / 2.0f), (int) (position.y - Game.HEIGHT / 2.0f));
		time++;
	}

	private boolean inElevator() {
		return getBounds().intersects(level.elevator.field);
	}

	private boolean enemyCollision() {
		for (int i = 0; i < level.enemys.size(); i++) {
			if (getBounds().intersects(level.enemys.get(i).getBounds())) {
				xxa += level.enemys.get(i).xa * 5;
				yya += level.enemys.get(i).ya * 5;
				return true;
			}
		}
		return false;
	}

	private void shoot() {
		if (!hasWeapon)
			return;

		if (fireRate < 0 && ammo > 0) {
			float dx = (float) (Math.cos(angleMouse()) * (12) - Math.sin(angleMouse()) * (-1.0));
			float dy = (float) (Math.sin(angleMouse()) * (12) + Math.cos(angleMouse()) * (-1.0));
			if (fireCount == 0) {
				top = fire1;
				fireCount++;
				Sound.gun.playAsSoundEffect(1.0f, 1.0f, false);
				Projectile p = new Projectile(position.x + dx, position.y + dy, (float) (rot - Math.toRadians(180)));
				level.add(p);

			} else if (fireCount == 1) {
				Sound.gun.playAsSoundEffect(1.0f, 1.0f, false);
				Projectile p = new Projectile(position.x - dx, position.y - dy, (float) (rot - Math.toRadians(180)));
				level.add(p);
				top = fire2;
				fireCount = 0;
			}
			ammo--;
			fireRate = 5;
			MyCursor.scale += .5f;
		}
	}

	public void render() {

		Shader.BASIC.enable();
		Shader.BASIC.setUniformMatrix("vw_matrix", Camera.getViewMatrix());
		Shader.BASIC.setUniformMatrix("ml_matrix", Matrix4f.translate(position).multiply(Matrix4f.rotateRad((angleMouse()))));

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		mesh.bind(Shader.BASIC);

		runAnim.getTexture().bind();
		mesh.draw();

		runAnim.getTexture().unbind();

		top.bind();
		mesh.draw();
		top.unbind();

		glDisable(GL_BLEND);
		Shader.BASIC.disable();

	}

}

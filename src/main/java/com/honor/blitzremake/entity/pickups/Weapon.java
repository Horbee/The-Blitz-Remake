package com.honor.blitzremake.entity.pickups;

import java.awt.Rectangle;

import com.honor.blitzremake.Resources;
import com.honor.blitzremake.entity.Entity;
import com.honor.blitzremake.entity.Player;
import com.honor.blitzremake.entity.TexturedModel;
import com.honor.blitzremake.graphics.Light;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.hud.HUD;
import com.honor.blitzremake.level.Level;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;
import com.honor.blitzremake.sound.Sound;

public class Weapon extends Entity {

	private Light light;
	private Vector3f scale;
	
	public Weapon(float x, float y) {
		super(x, y);
		model = new TexturedModel(Resources.weapon_mesh, Texture.weapon);
		model.hasTransparency = true;
		w = 80.0f;
		h = 33.0f;
		light = new Light(x, y, 0.3f, 0.1f, 0x02FF1B, 5.0f);
		light.flickering = true;
		scale = new Vector3f(1f, -1f, 1f);
	}

	@Override
	public void update() {
		light.update();

		if (getBounds().intersects(level.player.getBounds())) {
			pickedUp();
		}
	}

	private void pickedUp() {
		Sound.pickup3.playAsSoundEffect(1.0f, 1.0f, false);
		Player.hasWeapon = true;
		Level.phase++;
		HUD.resetTime();
		remove();
	}

	public Rectangle getBounds() {
		return new Rectangle((int) position.x, (int) position.y + 10, (int) w, (int) h);
	}

	public Matrix4f getModelMatrix() {
		return Matrix4f.translate(position).multiply(Matrix4f.scale(scale));
	}

	public Light getLight() {
		return light;
	}

}

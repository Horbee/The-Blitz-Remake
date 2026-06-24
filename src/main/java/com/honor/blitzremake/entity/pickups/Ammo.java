package com.honor.blitzremake.entity.pickups;

import java.awt.Rectangle;
import com.honor.blitzremake.Resources;
import com.honor.blitzremake.entity.Entity;
import com.honor.blitzremake.entity.Player;
import com.honor.blitzremake.entity.TexturedModel;
import com.honor.blitzremake.entity.particle.AmmoFXEmitter;
import com.honor.blitzremake.graphics.Light;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;
import com.honor.blitzremake.sound.Sound;
import com.honor.blitzremake.util.Util;

public class Ammo extends Entity {

	private float time;
	private Light light;
	private AmmoFXEmitter ammofx_emitter;

	public Ammo(float x, float y) {
		super(x, y);
		model = new TexturedModel(Resources.pickup_mesh, Texture.ammo);
		model.hasTransparency = true;
		w = 64.0f;
		h = 40.0f;
		light = new Light(position.x, position.y, 0.3f, 0.2f, 0xffff0000, 10.0f);
		light.flickering = true;
		
		ammofx_emitter = new AmmoFXEmitter(x, y);

	}

	public void update() {
		if (getBounds().intersects(level.player.getBounds())) {
			pickedUp();
		}
		time += 0.1f;
		light.update();
	}

	private void pickedUp() {
		Sound.pickup1.playAsSoundEffect(1.0f, 1.0f, false);
		Player.ammo += 60;
		AmmoFXEmitter.ammoFXEmitters.remove(ammofx_emitter);
		remove();
	}

	public Rectangle getBounds() {
		return new Rectangle((int) position.x, (int) position.y + 10, (int) w, (int) h);
	}

	public Matrix4f getModelMatrix() {
		float scale = (float) (Math.abs(Math.sin(time * 0.2)) + 0.3f);
		scale = Util.Clamp(scale, 0.3f, 1.0f);
		return Matrix4f.translate(position).multiply(Matrix4f.scale(new Vector3f(scale, scale, 1)));
	}
	
	public Light getLight() {
		return light;
	}
	
	public AmmoFXEmitter getEmitter() {
		return ammofx_emitter;
	}


}

package com.honor.blitzremake.entity.others;

import java.awt.Rectangle;
import com.honor.blitzremake.Resources;
import com.honor.blitzremake.entity.Entity;
import com.honor.blitzremake.entity.TexturedModel;
import com.honor.blitzremake.entity.particle.BlowFXEmitter;
import com.honor.blitzremake.entity.particle.SmokeFXEmitter;
import com.honor.blitzremake.graphics.Light;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.hud.HUD;
import com.honor.blitzremake.level.Level;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;
import com.honor.blitzremake.sound.Sound;

public class Generator extends Entity{

	public Rectangle field = new Rectangle();
	private Vector3f scale;
	private Light light;
	public int health = 5;
	public boolean destroyed = false;
	
	private BlowFXEmitter blowfx_emitter;
	
	public Generator(float x, float y) {
		super(x, y);
		position.z = 0.1f;
		field.setBounds((int) position.x - 50, (int) position.y, 100, 30);
		model = new TexturedModel(Resources.generator_mesh, Texture.generator);
		model.hasTransparency = true;
		w = 100.0f;
		h = 100.0f;
		scale = new Vector3f(1f, -1f, 1f);
		light = new Light(x, y, 0.3f, 0.3f, 0xffffb557, 15.0f);
		light.flickering = false;
		
		
	}
	
	@Override
	public void update() {
		if(destroyed) return;
		
		if(health <= 0) {
			destroyed = true;
			blowfx_emitter = new BlowFXEmitter(position.x, position.y);	
			Sound.explosion.playAsSoundEffect(1f, 1f, false);
		}
		
		light.update();
		if(destroyed) {
			light.intensity = 0.1f;
			Level.phase++;
			HUD.resetTime();
		}
		
	}
	
	public Rectangle getBounds() {
		return field;
	}

	public Matrix4f getModelMatrix() {
		return Matrix4f.translate(position).multiply(Matrix4f.scale(scale));
	}
	
	public Light getLight() {
		return light;
	}
	
}

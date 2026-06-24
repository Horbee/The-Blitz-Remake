package com.honor.blitzremake.graphics;

import com.honor.blitzremake.entity.Entity;
import com.honor.blitzremake.level.Level;
import com.honor.blitzremake.math.Vector3f;

public class Light extends Entity{

	public Vector3f vec_color = new Vector3f();
	public int color;
	public float intensity, intensityModifier;
	public float radius = 0.4f;
	public Level level;
	public boolean flickering = false;
	public boolean fadeOut = false;
	private float time;

	public Light(float x, float y, int color, float intensity) {
		super(x, y);
		position.z = 0.3f;
		this.color = color;
		this.intensity = intensity;
		this.vec_color = new Vector3f(((color & 0xff0000) >> 16) / 255.0f + radius, ((color & 0xff00) >> 8) / 255.0f + radius, (color & 0xff) / 255.0f + radius);
	}
	
	public Light(float x, float y, float z, float radius, int color, float intensity) {
		super(x, y);
		position.z = z;
		this.radius = radius;
		this.color = color;
		this.intensity = intensity;
		this.vec_color = new Vector3f(((color & 0xff0000) >> 16) / 255.0f + radius, ((color & 0xff00) >> 8) / 255.0f + radius, (color & 0xff) / 255.0f + radius);
	}
	

	public Light(float x, float y, int color) {
		super(x, y);
		this.color = color;
		this.intensity = 1.0f;
		this.vec_color = new Vector3f(((color & 0xff0000) >> 16) / 255.0f + radius, ((color & 0xff00) >> 8) / 255.0f + radius, (color & 0xff) / 255.0f + radius);
	}

	public void init(Level level) {
		this.level = level;
	}

	public void update() {
		if (flickering) {
			intensity += Math.sin(time * 0.8) * 0.5;
		}
		if (fadeOut) {
			if (intensity > 5.0f) intensity -= time * 3;
		}
		time += 0.1;
	}
	
	public void move(float nx, float ny) {
		position.x += nx;
		position.y += ny;
	}
	
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
	
	
}

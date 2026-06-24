package com.honor.blitzremake.entity;

import java.awt.Rectangle;

import com.honor.blitzremake.level.Level;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;

public abstract class Entity {

	protected TexturedModel model;

	public Vector3f position = new Vector3f();
	public boolean isRemoved = false;
	protected Level level;
	protected float w, h;

	public Entity() {

	}

	public Entity(float x, float y) {
		setPosition(x, y);
	}

	public void init(Level level) {
		this.level = level;
	}

	protected void remove() {
		isRemoved = true;
	}

	public Rectangle getBounds() {
		return new Rectangle((int) position.x, (int) position.y, (int) w, (int) h);
	}

	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
	}

	public abstract void update();

	public void render(){
		
	}

	public Matrix4f getModelMatrix() {
		return null;
	}

	public TexturedModel getModel() {
		return model;
	}

}

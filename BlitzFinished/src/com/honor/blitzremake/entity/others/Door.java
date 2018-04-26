package com.honor.blitzremake.entity.others;

import java.awt.Rectangle;

import com.honor.blitzremake.entity.Entity;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.graphics.VertexArray;
import com.honor.blitzremake.level.Level;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;
import com.honor.blitzremake.sound.Sound;

public class Door extends Entity {

	public Rectangle doorfield = new Rectangle();
	public Collider collider;

	private VertexArray meshdoor = new VertexArray(142, 68, 0.1f);
	private Texture door2 = new Texture("res/img/door2.png");

	private float doorOffset;
	private boolean shouldOpen = false;

	private boolean lastFrame = false;

	public Door(float x, float y) {
		super(x, y);
		position.z = 0.3f;
		doorfield.setBounds((int) position.x - 80, (int) position.y - 30, 200, 195);
		collider = new Collider(position.x, position.y, 213, 68);
	}

	@Override
	public void init(Level level) {
		super.init(level);
		level.colliders.add(collider);
	}

	@Override
	public void update() {
		shouldOpen = doorfield.intersects(level.player.getBounds());
		
		if (shouldOpen != lastFrame) {
			Sound.door.stop();
			Sound.door.playAsSoundEffect(1f, 1f, false);			
		}
		
		lastFrame = shouldOpen;

		if (shouldOpen) {
			if (doorOffset < 200) {
				doorOffset += 1.5f;
			}
		} else {
			if (doorOffset > 0) {
				doorOffset -= 1.5f;
			}
		}
		collider.setPosition(position.x - doorOffset, position.y);
	}

	public void render() {

		meshdoor.bind(Shader.LEVEL);
		Shader.LEVEL.setUniformMatrix("ml_matrix", Matrix4f.translate(position));
		Shader.LEVEL.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(position.x - doorOffset, position.y, position.z)).multiply(Matrix4f.scale(new Vector3f(1.5f, 1f, 1))));
		door2.bind();
		meshdoor.draw();
		door2.unbind();

	}

	public Matrix4f getModelMatrix() {
		return Matrix4f.translate(new Vector3f(position.x, position.y, 0.1f));
	}

}

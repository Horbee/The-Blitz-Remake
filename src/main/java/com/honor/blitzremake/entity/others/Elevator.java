package com.honor.blitzremake.entity.others;

import java.awt.Rectangle;
import com.honor.blitzremake.entity.Entity;
import com.honor.blitzremake.entity.Player;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.graphics.VertexArray;
import com.honor.blitzremake.hud.HUD;
import com.honor.blitzremake.level.Level;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;

public class Elevator extends Entity {

	public Rectangle field = new Rectangle();
	public Rectangle doorfield = new Rectangle();
	private VertexArray meshfloor = new VertexArray(200, 200, 0.1f);
	private VertexArray meshdoor = new VertexArray(142, 68, 0.1f);
	private Texture floor1 = new Texture("res/img/floor1.png");
	private Texture door1 = new Texture("res/img/door1.png");
	private Texture door2 = new Texture("res/img/door2.png");
	private boolean open = false;
	private float doorOffset;

	public Collider collider_left;
	public Collider collider_right;
	
	public Elevator(float x, float y) {
		super(x, y);
		position.z = 0.3f;
		field.setBounds((int) position.x - 100, (int) position.y - 100, 200, 200);
		doorfield.setBounds((int) position.x - 80, (int) position.y + 100, 200, 95);
		collider_left = new Collider(position.x - 68, position.y + 90, 142, 48);
		collider_right = new Collider(position.x  + 68, position.y + 90, 142, 48);
	}
	
	@Override
	public void init(Level level) {
		super.init(level);
		level.colliders.add(collider_left);
		level.colliders.add(collider_right);
	}

	public void update() {
		open = doorfield.intersects(level.player.getBounds());

		if (open && Player.pickedUpPowerCells < 8) {
			if(Level.phase == 2)
				HUD.resetTime();
			return;
		}

		if (open) {
			if (doorOffset < 50) {
				doorOffset += 1.5f;
			}
		} else {
			if (doorOffset > 0) {
				doorOffset -= 1.5f;
			}
		}
		collider_left.setPosition(position.x  - 68 - doorOffset, position.y + 90);
		collider_right.setPosition(position.x + 68 + doorOffset, position.y + 90);
	}

	public void render() {
		
		Shader.LEVEL.setUniformMatrix("ml_matrix", Matrix4f.translate(position));
		floor1.bind();
		meshfloor.render(Shader.LEVEL);
		floor1.unbind();
		
		meshdoor.bind(Shader.LEVEL);
		Shader.LEVEL.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(position.x + 68 + doorOffset, position.y + 90, position.z)).multiply(Matrix4f.scale(new Vector3f(1,0.7f,1))));
		door1.bind();
		meshdoor.draw();
		door1.unbind();

		Shader.LEVEL.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(position.x - 68 - doorOffset, position.y + 90, position.z)).multiply(Matrix4f.scale(new Vector3f(1,0.7f,1))));
		door2.bind();
		meshdoor.draw();
		door2.unbind();
		
	}
	
	public Matrix4f getModelMatrix() {
		return Matrix4f.translate(new Vector3f(position.x, position.y, 0.1f));
	}

}

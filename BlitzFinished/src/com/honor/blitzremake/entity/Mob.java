package com.honor.blitzremake.entity;

import java.awt.Point;

import com.honor.blitzremake.entity.others.Collider;
import com.honor.blitzremake.graphics.VertexArray;
import com.honor.blitzremake.level.Wall;

public class Mob extends Entity {

	protected VertexArray mesh;
	protected float SIZE = 64.0f;

	public Mob(float x, float y) {
		super(x, y);
	}

	protected void move(float xa, float ya) {
		position.x += xa;
		position.y += ya;

		if (Player.pickedUpPowerCells < 12) {
			// position.x = Util.Clamp(position.x, +32, 1248);
			// position.y = Util.Clamp(position.y, +32, 1248);
		}
	}

	protected boolean collision(float xa, float ya) {
		boolean collide = false;

		float npx = position.x + (xa * (w / 6));
		float npy = position.y + (ya * (h / 6));

		Point nextMobPos = new Point((int) npx, (int) npy);

		for (int i = 0; i < level.walls.size(); i++) {
			Wall w = level.walls.get(i);
			if (w.getBounds().contains(nextMobPos)) {
				collide = true;
				break;
			}
		}

		for (int i = 0; i < level.colliders.size(); i++) {
			Collider c = level.colliders.get(i);
			if (c.getBounds().contains(nextMobPos)) {
				collide = true;
				break;
			}
		}

		return collide;

	}

	public void update() {

	}

	public void render() {

	}

}

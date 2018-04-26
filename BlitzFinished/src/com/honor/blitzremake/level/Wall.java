package com.honor.blitzremake.level;

import java.awt.Rectangle;

import com.honor.blitzremake.Resources;
import com.honor.blitzremake.entity.Entity;
import com.honor.blitzremake.entity.TexturedModel;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;

public class Wall extends Entity {

	public Wall(float x, float y) {
		super(x, y);
		w = 130;
		h = 130;
		model = new TexturedModel(Resources.wall_mesh, Texture.wall);
	}

	public void render() {
		Texture.wall.bind();
		Shader.LEVEL.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(position.x, position.y, 0.1f)));
		Resources.wall_mesh.render(Shader.LEVEL);;
		Texture.wall.unbind();
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (position.x - w / 2), (int) (position.y - h / 2), (int) w, (int) h);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	public Matrix4f getModelMatrix() {
		return Matrix4f.translate(new Vector3f(position.x, position.y, 0.1f));
	}
}

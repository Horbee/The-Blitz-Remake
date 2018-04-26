package com.honor.blitzremake.entity;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import com.honor.blitzremake.Game;
import com.honor.blitzremake.Resources;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;

public class MyCursor {

	public static float scale = 1.0f;

	public static void update() {
		if (scale > 1.0) {
			scale -= 0.1;
		}
	}

	public static void render() {

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE);

		Shader.HEALTH.enable();
		Shader.HEALTH.setUniform3f("scale", new Vector3f(1, 1, 1));
		Shader.HEALTH.setUniform1f("healthIndicator", 1);
		Shader.HEALTH.setUniformMatrix("ml_matrix", getTransformation());

		Texture.cursor.bind();
		Resources.hp_mesh.render(Shader.HEALTH);
		Texture.cursor.unbind();

		Shader.HEALTH.disable();

		glDisable(GL_BLEND);
	}

	private static Matrix4f getTransformation() {
		return Matrix4f.translate(new Vector3f(Mouse.getX(), Display.getDisplayMode().getHeight() - Mouse.getY(), 0)).multiply(Matrix4f.rotateAngle(Game.time)).multiply(Matrix4f.scale(new Vector3f(scale, scale, 1.0f)));
	}

}

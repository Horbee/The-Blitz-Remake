package com.honor.blitzremake.menu;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.input.Keyboard;
import com.honor.blitzremake.Game;
import com.honor.blitzremake.Resources;
import com.honor.blitzremake.State;
import com.honor.blitzremake.font.Font;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.graphics.VertexArray;
import com.honor.blitzremake.input.Input;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;

public class Menu {

	Texture bg = new Texture("res/img/menuBkg.png");
	Texture logo = new Texture("res/img/logo.png");
	VertexArray mesh = new VertexArray(900, 500, 0.1f);
	VertexArray logo_mesh = new VertexArray(200, 80, 0.1f);
	private boolean fadeOut = false;
	private int time;

	public void update() {
		time++;
		//if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Input.controller.isButtonPressed(0)) {
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			time = 0;
			fadeOut = true;
		}
		if (fadeOut && time > 120) {
			State.setState(State.GAME);
		}
	}

	public void render() {
		Shader.MENU.enable();
		Shader.MENU.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(450, 250, 0.3f)));
		bg.bind();
		mesh.render(Shader.MENU);
		bg.unbind();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		logo.bind();
		Shader.MENU.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(100, 450, 0)).multiply(Matrix4f.scale(new Vector3f(1.0f, -1.0f, 1.0f))));
		logo_mesh.render(Shader.MENU);
		logo.unbind();
		glDisable(GL_BLEND);
		
		Shader.MENU.disable();

		float blend = (float) Math.abs(Math.sin(time * 0.05));
		Font.blend = blend;
		Font.drawString("PRESS SPACE TO PLAY", Game.WIDTH / 2 - 250, 190, 15, -13, new Vector3f(1.0f, 0.8f, 0.3f));
		
		
		if (fadeOut) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			Shader.FADE.enable();
			
			Shader.FADE.setUniform1f("OUT", 1);
			Shader.FADE.setUniform1f("time", time);

			Resources.fade_mesh.bindLightMesh(Shader.FADE);
			Resources.fade_mesh.draw();
			
			Shader.FADE.disable();
			glDisable(GL_BLEND);
		}
	}

}

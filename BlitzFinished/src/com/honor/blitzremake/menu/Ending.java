package com.honor.blitzremake.menu;

import org.lwjgl.input.Keyboard;
import com.honor.blitzremake.Game;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.graphics.VertexArray;
import com.honor.blitzremake.input.Input;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;

public class Ending {

	Texture bg = Texture.endingScreen;
	VertexArray mesh = new VertexArray(900, 500, 0.1f);
	
	Game game;
	
	public Ending(Game game) {
		this.game = game;
	}
	
	public void update() {
//		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Input.controller.isButtonPressed(0)) {
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			game.reset();
		}
	}

	public void render() {
		Shader.MENU.enable();
		Shader.MENU.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(450, 250, 0.3f)).multiply(Matrix4f.scale(new Vector3f(1f, -1f, 1f))));
		bg.bind();
		mesh.render(Shader.MENU);
		bg.unbind();
				
		Shader.MENU.disable();
	}

}

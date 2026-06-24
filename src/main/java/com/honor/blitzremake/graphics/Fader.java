package com.honor.blitzremake.graphics;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import com.honor.blitzremake.Resources;

public class Fader {

	private boolean fadeOUT;
	private float time;

	public boolean finished;

	public Fader(boolean fadeOUT) {
		this.fadeOUT = fadeOUT;
		// ha OUT akkor time++
		// ha IN akkor 1-time++
	}

	public void update() {
		if (finished)
			return;
		
		time++;
		if (time > 120) {
			finished = true;
		}
	}

	public void render() {
		if (finished)
			return;

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		Shader.FADE.enable();

		if (fadeOUT)
			Shader.FADE.setUniform1f("OUT", 1f);
		else
			Shader.FADE.setUniform1f("OUT", 0f);

		Shader.FADE.setUniform1f("time", time);

		Resources.fade_mesh.bindLightMesh(Shader.FADE);
		Resources.fade_mesh.draw();

		Shader.FADE.disable();
		glDisable(GL_BLEND);
	}

	public void reset(boolean fadeOUT) {
		this.fadeOUT = fadeOUT;
		finished = false;
		time = 0;
	}

}

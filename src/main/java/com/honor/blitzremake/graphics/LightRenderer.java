package com.honor.blitzremake.graphics;

import static org.lwjgl.opengl.GL11.*;
import java.util.List;
import com.honor.blitzremake.Resources;
import com.honor.blitzremake.camera.Camera;
import com.honor.blitzremake.level.Level;
import com.honor.blitzremake.math.Matrix4f;

public class LightRenderer {

	public LightRenderer() {

	}

	public void render(List<Light> lights) {
		Shader.LIGHT.enable();
		Shader.LIGHT.setUniformMatrix("vw_matrix", Camera.getViewMatrix());
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);
		Resources.light_mesh.bind(Shader.LIGHT);
		
		for (Light light : lights) {
			prepareLight(light);
			Resources.light_mesh.draw();
		}
		
		glDisable(GL_BLEND);
		Shader.LIGHT.disable();
	}

	private void prepareLight(Light light) {
		//FOR EACH LIGHT
		light.radius = 200.0f;
		Shader.LIGHT.setUniformMatrix("ml_matrix", Matrix4f.translate(light.position));
		Shader.LIGHT.setUniform1f("radius", light.radius);
		Shader.LIGHT.setUniform2f("lightPosition", light.position.x - Level.xOffset, light.position.y - Level.yOffset);
		Shader.LIGHT.setUniform3f("lightColor", light.vec_color);
		Shader.LIGHT.setUniform1f("lightIntensity", light.intensity);
		
	}

}

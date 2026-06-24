package com.honor.blitzremake.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.util.List;
import java.util.Map;

import com.honor.blitzremake.camera.Camera;
import com.honor.blitzremake.entity.Enemy;
import com.honor.blitzremake.entity.Entity;
import com.honor.blitzremake.entity.Projectile;
import com.honor.blitzremake.entity.SpawnerPipe;
import com.honor.blitzremake.entity.TexturedModel;
import com.honor.blitzremake.entity.others.Collider;
import com.honor.blitzremake.entity.others.Generator;
import com.honor.blitzremake.entity.pickups.Ammo;
import com.honor.blitzremake.entity.pickups.PowerCell;
import com.honor.blitzremake.entity.pickups.Weapon;
import com.honor.blitzremake.level.Wall;
import com.honor.blitzremake.math.Matrix4f;

public class EntityRenderer {

	private Shader shader;

	public void render(Map<TexturedModel, List<Entity>> entities) {
		for (TexturedModel model : entities.keySet()) {
			List<Entity> batch = entities.get(model);
			if (batch.get(0) instanceof Enemy) shader = Shader.BASIC;
			if (batch.get(0) instanceof Ammo) shader = Shader.BASIC;
			if (batch.get(0) instanceof PowerCell) shader = Shader.BASIC;
			if (batch.get(0) instanceof Projectile) shader = Shader.PROJECTILE;
			if (batch.get(0) instanceof SpawnerPipe) shader = Shader.LEVEL;
			if (batch.get(0) instanceof Wall) shader = Shader.LEVEL;
			if (batch.get(0) instanceof Weapon) shader = Shader.BASIC;
			if (batch.get(0) instanceof Generator) shader = Shader.LEVEL;
			if (batch.get(0) instanceof Collider) shader = Shader.BASIC;
			shader.enable();
			shader.setUniformMatrix("vw_matrix", Camera.getViewMatrix());

			prepareTexturedModel(model);
			for (Entity entity : batch) {
				prepareInstance(entity);
				model.getMesh().draw();
			}
			unbindTexturedModel();
		}
	}

	private void prepareTexturedModel(TexturedModel model) {
		if (model.hasTransparency) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		}
		model.getTexture().bind();
		model.getMesh().bind(shader);
	}

	private void unbindTexturedModel() {
		glBindTexture(GL_TEXTURE_2D, 0);
		glUseProgram(0);
		glDisable(GL_BLEND);
	}

	private void prepareInstance(Entity entity) {
		// CREATING MODELMATRIX FOR EACH ENTITY3
		Matrix4f transformationMatrix = entity.getModelMatrix();
		shader.setUniformMatrix("ml_matrix", transformationMatrix);
	}

}

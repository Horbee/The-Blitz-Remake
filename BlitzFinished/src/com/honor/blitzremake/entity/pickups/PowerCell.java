package com.honor.blitzremake.entity.pickups;

import java.awt.Rectangle;
import com.honor.blitzremake.Resources;
import com.honor.blitzremake.entity.Entity;
import com.honor.blitzremake.entity.Player;
import com.honor.blitzremake.entity.TexturedModel;
import com.honor.blitzremake.graphics.Light;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.sound.Sound;

public class PowerCell extends Entity {

	private Light light;

	public PowerCell(float x, float y) {
		super(x, y);
		model = new TexturedModel(Resources.pickup_mesh, Texture.powercell);
		model.hasTransparency = true;
		w = 64.0f;
		h = 40.0f;
		light = new Light(x, y, 0.3f, 0.2f, 0xffffb557, 10.0f);
		light.flickering = true;

	}

	public void update() {
		light.update();

		if (getBounds().intersects(level.player.getBounds())) {
			pickedUp();
		}

	}

	public void render() {

		/*glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		Shader.HEALTH.enable();
		Shader.HEALTH.setUniformMatrix("vw_matrix", Camera.getViewMatrix());
		Shader.HEALTH.setUniformMatrix("ml_matrix", Matrix4f.translate(position));
		Shader.HEALTH.setUniform3f("scale", new Vector3f(1, 1, 1));
		Texture.powercell.bind();
		Resources.pickup_mesh.render(Shader.HEALTH);
		Texture.powercell.unbind();

		Shader.HEALTH.disable();
		glDisable(GL_BLEND);*/

		//light.render();

	}

	private void pickedUp() {
		Sound.pickup2.playAsSoundEffect(1.0f, 1.0f, false);
		Player.pickedUpPowerCells++;
		remove();
	}

	public Rectangle getBounds() {
		return new Rectangle((int) position.x, (int) position.y + 10, (int) w, (int) h);
	}
	
	public Matrix4f getModelMatrix() {
		return Matrix4f.translate(position);
	}

	public Light getLight() {
		return light;
	}

}

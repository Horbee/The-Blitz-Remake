package com.honor.blitzremake.hud;

import static org.lwjgl.opengl.GL11.*;

import com.honor.blitzremake.Game;
import com.honor.blitzremake.Resources;
import com.honor.blitzremake.entity.Player;
import com.honor.blitzremake.font.Font;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.level.Level;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;

public class HUD {
	
	public static int health = 10;
	public static float time;
	public static float labelTime;
	private static int positionx = (Game.WIDTH / 2) - 125;
	private static float scale;

	public static void update() {
		time += 0.1f;
		labelTime += 0.1f;
	}

	public static void render() {
		Shader.HEALTH.enable();

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	
		Shader.HEALTH.setUniformMatrix("vw_matrix", Matrix4f.identity());
		Texture.hp.bind();
		Resources.hp_mesh.bind(Shader.HEALTH);
		for (int i = 0; i < 10; i++) {
			scale =  (float) Math.sin(time * 0.3 + i * 0.3) * 1.5f;
			//scale = Util.Clamp(scale, 0.4f, 2.0f);
			Shader.HEALTH.setUniform3f("scale", new Vector3f(1, scale, 1));
			Shader.HEALTH.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(positionx + i * 25, 50, 0)));
			if (health <= i) Shader.HEALTH.setUniform1f("healthIndicator", 0.2f);
			else Shader.HEALTH.setUniform1f("healthIndicator", 1.0f);
			Resources.hp_mesh.draw();
		}

		Texture.hp.unbind();
		glDisable(GL_BLEND);

		Shader.HEALTH.disable();
		
		
		if(Player.hasWeapon) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			Shader.BASIC.enable();
			Texture.weapon.bind();
			Shader.BASIC.setUniformMatrix("vw_matrix", Matrix4f.identity());
			Shader.BASIC.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(55, 25, 0)).multiply(Matrix4f.scale(new Vector3f(1f, -1f, 1f))));
			Resources.weapon_mesh.render(Shader.BASIC);
			Texture.weapon.unbind();
			Shader.BASIC.disable();
			glDisable(GL_BLEND);
			
			Font.blend = 1.0f;
			Font.drawString("AMMO: " + Player.ammo, 90, 20, 15, -10, new Vector3f(1, .35f, .25f));
		}
		
		if(Level.phase >= 2) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			Shader.BASIC.enable();
			Texture.powercell.bind();
			Shader.BASIC.setUniformMatrix("vw_matrix", Matrix4f.identity());
			Shader.BASIC.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(55, Game.HEIGHT - 35, 0)).multiply(Matrix4f.scale(new Vector3f(1f, -1f, 1f))));
			Resources.pickup_mesh.render(Shader.BASIC);
			Texture.powercell.unbind();
			Shader.BASIC.disable();
			glDisable(GL_BLEND);
			
			Font.blend = 1.0f;
			Font.drawString("PC: " + Player.pickedUpPowerCells + " FROM 8", 90, Game.HEIGHT - 155, 15, -10, new Vector3f(1, .35f, .25f));
		}
	
		
		if (labelTime < 16 && Level.phase == 0) {
			Font.drawString("PICK UP THE WEAPON AND AMMO", Game.WIDTH / 2 - 288, 100, 15, -13, new Vector3f(1, .65f, .25f));
		}
		
		if (labelTime < 16 && Level.phase == 1) {
			Font.drawString("DESTROY THE GENERATOR TO OPEN THE DOOR", Game.WIDTH / 2 - 375, 100, 15, -13, new Vector3f(1, .65f, .25f));
		}
		
		if (labelTime < 16 && Level.phase == 2) {
			Font.drawString("GET ALL ENERGY CELLS!", Game.WIDTH / 2 - 235, 100, 15, -13, new Vector3f(1, .65f, .25f));
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			Shader.BASIC.enable();
			Texture.powercell.bind();
			Shader.BASIC.setUniformMatrix("vw_matrix", Matrix4f.identity());
			Shader.BASIC.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(Game.WIDTH / 2 , 170, 0)));
			Resources.pickup_mesh.render(Shader.BASIC);
			Texture.powercell.unbind();
			Shader.BASIC.disable();
			glDisable(GL_BLEND);
		}
		
		if (labelTime < 16 && Level.phase == 3) {
			Font.drawString("WELL DONE! GET TO THE ELEVATOR!", Game.WIDTH / 2 - 320, 100, 15, -10, new Vector3f(1, .65f, .25f));
		}
	}
	
	public static void resetTime() {
		labelTime = 0f;
	}

}

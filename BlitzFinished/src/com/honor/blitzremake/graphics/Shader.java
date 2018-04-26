 package com.honor.blitzremake.graphics;

import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;

import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;
import com.honor.blitzremake.util.ShaderUtils;

public class Shader {

	public static int VERTEX_ATTRIB = 0;
	public static int TEXTURE_ATTRIB = 1;
	
	public static Shader MENU, BASIC, PROJECTILE, LEVEL, LIGHT, HEALTH, PARTICLE, FONT, FADE, BLOODSPLAT, SPARKLE;
	
	private int ID;
	private Map<String, Integer> locationCache = new HashMap<String, Integer>();
	
	
	private Shader(String vertPath, String fragPath) {
		ID = ShaderUtils.load(vertPath, fragPath);
	}
	
	public static void loadAll() {
		MENU = new Shader("shaders/menu.vs", "shaders/menu.fs");
		BASIC = new Shader("shaders/basic.vs", "shaders/basic.fs");
		PROJECTILE = new Shader("shaders/projectile.vs", "shaders/projectile.fs");
		LEVEL = new Shader("shaders/level.vs", "shaders/level.fs");
		LIGHT = new Shader("shaders/light.vs", "shaders/light.fs");
		HEALTH = new Shader("shaders/health.vs", "shaders/health.fs");
		PARTICLE = new Shader("shaders/particle.vs", "shaders/particle.fs");
		FONT = new Shader("shaders/font.vs", "shaders/font.fs");
		FADE = new Shader("shaders/fade.vs", "shaders/fade.fs");
		BLOODSPLAT = new Shader("shaders/bloodsplat.vs", "shaders/bloodsplat.fs");
		SPARKLE = new Shader("shaders/sparkle.vs", "shaders/sparkle.fs");
	}
	
	public int getAttribLocation(String name) {
		return glGetAttribLocation(ID, name);
	}
	
	private int getUniformLocation(String name) {
		if (locationCache.containsKey(name))
			return locationCache.get(name);
		
		int result = glGetUniformLocation(ID, name);
		if (result == -1) 
			System.err.println("Could not find uniform variable '" + name + "'!");
		else
			locationCache.put(name, result);
		return result;
	}
	
	public void setUniform1i(String name, int value) {
		glUniform1i(getUniformLocation(name), value);
	}
	
	public void setUniform1f(String name, float value) {
		glUniform1f(getUniformLocation(name), value);
	}
	
	public void setUniform2f(String name, float x, float y) {
		glUniform2f(getUniformLocation(name), x, y);
	}
	
	public void setUniform3f(String name, Vector3f vector) {
		glUniform3f(getUniformLocation(name), vector.x, vector.y, vector.z);
	}
	
	public void setUniformMatrix(String name, Matrix4f matrix) {
		glUniformMatrix4(getUniformLocation(name), false, matrix.toFloatBuffer());
	}
	
	public void enable() {
		glUseProgram(ID);
	}
	
	public void disable() {
		glUseProgram(0);
	}
	
	public int getID() {
		return ID;
	}
	
}

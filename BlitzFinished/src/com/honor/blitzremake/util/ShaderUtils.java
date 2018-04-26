package com.honor.blitzremake.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ShaderUtils {

	public static int load(String vertPath, String fragPath) {
		String vs = FileUtils.loadAsString(vertPath);
		String fs = FileUtils.loadAsString(fragPath);
		return create(vs, fs);
	}
	
	public static int create(String vertString, String fragString) {
		int program = glCreateProgram();
		int vsID = glCreateShader(GL_VERTEX_SHADER);
		int fsID = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(vsID, vertString);
		glShaderSource(fsID, fragString);
		
		glCompileShader(vsID);
		if (glGetShaderi(vsID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failed to compile vertex shader!");
			System.err.println(glGetShaderInfoLog(vsID, 2048));
		}else{
			System.out.println("Vertex shader compiled");
		}
		
		glCompileShader(fsID);
		if (glGetShaderi(fsID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failed to compile fragment shader!");
			System.err.println(glGetShaderInfoLog(fsID, 2048));
		}else{
			System.out.println("Fragment shader compiled");
		}
		
		
		glAttachShader(program, vsID);
		glAttachShader(program, fsID);
		glLinkProgram(program);
		glValidateProgram(program);
		
		glDeleteShader(vsID);
		glDeleteShader(fsID);
		
		return program;
	}
	
}

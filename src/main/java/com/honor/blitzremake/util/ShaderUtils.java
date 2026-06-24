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
			String log = glGetShaderInfoLog(vsID, 8192);
			glDeleteShader(vsID);
			glDeleteShader(fsID);
			throw new RuntimeException("Failed to compile vertex shader!\n" + log);
		}

		glCompileShader(fsID);
		if (glGetShaderi(fsID, GL_COMPILE_STATUS) == GL_FALSE) {
			String log = glGetShaderInfoLog(fsID, 8192);
			glDeleteShader(vsID);
			glDeleteShader(fsID);
			throw new RuntimeException("Failed to compile fragment shader!\n" + log);
		}

		glAttachShader(program, vsID);
		glAttachShader(program, fsID);
		glLinkProgram(program);

		if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
			String log = glGetProgramInfoLog(program, 8192);
			glDeleteProgram(program);
			glDeleteShader(vsID);
			glDeleteShader(fsID);
			throw new RuntimeException("Failed to link shader program!\n" + log);
		}

		glDeleteShader(vsID);
		glDeleteShader(fsID);

		return program;
	}

}
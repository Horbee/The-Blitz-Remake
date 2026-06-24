package com.honor.blitzremake.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import com.honor.blitzremake.util.BufferUtils;

public class VertexArray {

	// private int vao;
	private int vbo, ibo, tbo;
	private int count;

	public VertexArray(float[] vertices, byte[] indices, float[] textureCoordinates) {
		create(vertices, indices, textureCoordinates);
	}

	public VertexArray(float width, float height, float depth) {
		float[] vertices = new float[] { -width / 2.0f, -height / 2.0f, depth,//
				-width / 2.0f, height / 2.0f, depth, //
				width / 2.0f, height / 2.0f, depth,//
				width / 2.0f, -height / 2.0f, depth };

		byte[] indices = new byte[] { 0, 1, 2,//
				2, 3, 0 };

		float[] textureCoordinates = new float[] { 0, 1,//
				0, 0,//
				1, 0,//
				1, 1 };

		create(vertices, indices, textureCoordinates);
	}

	private void create(float[] vertices, byte[] indices, float[] textureCoordinates) {
		count = indices.length;

		// vao = glGenVertexArrays();
		// glBindVertexArray(vao);

		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(vertices), GL_STATIC_DRAW);
		glVertexAttribPointer(Shader.VERTEX_ATTRIB, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(Shader.VERTEX_ATTRIB);

		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtils.createByteBuffer(indices), GL_STATIC_DRAW);

		tbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, tbo);
		glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(textureCoordinates), GL_STATIC_DRAW);
		glVertexAttribPointer(Shader.TEXTURE_ATTRIB, 2, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(Shader.TEXTURE_ATTRIB);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		// glBindVertexArray(0);

	}

	public void bind(Shader shader) {

		glEnableVertexAttribArray(shader.getAttribLocation("position"));
		glEnableVertexAttribArray(shader.getAttribLocation("tc"));

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glVertexAttribPointer(shader.getAttribLocation("position"), 3, GL_FLOAT, false, 0, 0);

		glBindBuffer(GL_ARRAY_BUFFER, tbo);
		glVertexAttribPointer(shader.getAttribLocation("tc"), 2, GL_FLOAT, false, 0, 0);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

	}

	public void draw() {
		glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_BYTE, 0);
	}

	public void unbind(Shader shader) {
		glDisableVertexAttribArray(shader.getAttribLocation("position"));
		glDisableVertexAttribArray(shader.getAttribLocation("tc"));
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public void render(Shader shader) {
		bind(shader);
		draw();
	}

	public void bindParticle(Shader shader) {
		glEnableVertexAttribArray(shader.getAttribLocation("position"));

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glVertexAttribPointer(shader.getAttribLocation("position"), 3, GL_FLOAT, false, 0, 0);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
	}
	
	public void bindLightMesh(Shader shader) {
		glEnableVertexAttribArray(shader.getAttribLocation("position"));	
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glVertexAttribPointer(shader.getAttribLocation("position"), 3, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
	}


}

package com.honor.blitzremake.font;

import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.graphics.VertexArray;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class Font {

	private static int size = 32; // egy darab betû hány pixel
	private static int[] texIDs;
	private static VertexArray mesh;
	public static float blend = 1.0f;

	private static String chars = "0123456789" + //
			"ABCDEFGHIJ" + //
			"KLMNOPQRST" + //
			"UVWXYZ:!? ";

	public static void load() {
		texIDs = Texture.loadFont("res/img/NeoFont.png", 10, 4, size);
		mesh = new VertexArray(size, size, 0.0f);
	}

	public static void drawString(String text, int x, int y, int size, int spacing, Vector3f colorIN) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glActiveTexture(GL_TEXTURE0);
		Shader.FONT.enable();
		Shader.FONT.setUniform3f("colorIN", colorIN);
		float scale = size / 20.0f;
		int xx = x;
		int yy = y;
		for (int i = 0; i < text.length(); i++) {
			float xOffset = xx / scale;
			float yOffset = yy / scale;
			int currentChar = text.charAt(i);
			int index = chars.indexOf(currentChar);
			if (index >= 0 && currentChar != ' ') {
				// if (currentChar == 'p' || currentChar == 'g' || currentChar == 'q' || currentChar == 'y' || currentChar == ',') yOffset += 40;
				Shader.FONT.setUniform1f("blend", blend);
				Shader.FONT.setUniform3f("scale", new Vector3f(scale, scale, 1.0f));
				Shader.FONT.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(xOffset, yOffset, 1.0f)));
				glBindTexture(GL_TEXTURE_2D, texIDs[index]);

				mesh.render(Shader.FONT);

				glBindTexture(GL_TEXTURE_2D, 0);
			}
			if (currentChar == 'I') xx += (Font.size + spacing - 10) * scale;
			else xx += (Font.size + spacing) * scale;

		}
		Shader.FONT.disable();
		glDisable(GL_BLEND);
	}
}

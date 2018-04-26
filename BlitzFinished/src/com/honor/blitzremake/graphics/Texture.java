package com.honor.blitzremake.graphics;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.honor.blitzremake.util.BufferUtils;

public class Texture {

	private static List<Integer> textures = new ArrayList<Integer>();

	public static Texture blast = new Texture("res/img/blast1.png");
	public static Texture decal1 = new Texture("res/img/decal1.png");
	public static Texture decal3 = new Texture("res/img/decal3.png");
	public static Texture weapon = new Texture("res/img/weapon2.png");
	public static Texture generator = new Texture("res/img/generator.png");
	public static Texture ground = new Texture("res/img/ground2.png");
	public static Texture preLevel = new Texture("res/img/preLevel.png");
	public static Texture ammo = new Texture("res/img/ammo.png");
	public static Texture powercell = new Texture("res/img/powercell.png");
	public static Texture sparkle = new Texture("res/img/sparks.png");
	public static Texture hp = new Texture("res/img/healthBlip.png");
	public static Texture lightning1 = new Texture("res/img/lightning1.png");
	public static Texture lightning2 = new Texture("res/img/lightning2.png");
	public static Texture lightning3 = new Texture("res/img/lightning3.png");
	public static Texture lightning4 = new Texture("res/img/lightning4.png");
	public static Texture smoke1 = new Texture("res/img/smoke1.png");
	public static Texture smoke2 = new Texture("res/img/smoke2.png");
	public static Texture smoke3 = new Texture("res/img/smoke3.png");
	public static Texture smoke4 = new Texture("res/img/smoke4.png");
	public static Texture blow1 = new Texture("res/img/blow1.png");
	public static Texture blow2 = new Texture("res/img/blow2.png");
	public static Texture blow3 = new Texture("res/img/blow3.png");
	public static Texture blow4 = new Texture("res/img/blow4.png");
	public static Texture wall = new Texture("res/img/wall1.png");
	public static Texture wall2 = new Texture("res/img/wall2.png");
	public static Texture enemy = new Texture("res/img/monster.png");
	public static Texture cursor = new Texture("res/img/crosshair.png");
	public static Texture pipe = new Texture("res/img/pipe.png");
	public static Texture bloodSplat1 = new Texture("res/img/bloodSplat1.png");
	public static Texture blood1 = new Texture("res/img/blood1.png");
	public static Texture blood2 = new Texture("res/img/blood2.png");
	public static Texture blood3 = new Texture("res/img/blood3.png");
	public static Texture debug = new Texture("res/img/debug.png");
	public static Texture endingScreen = new Texture("res/img/endingScreen.png");

	private int width, height;
	private int ID;

	public Texture(String path) {
		ID = load(path);
	}

	private int load(String path) {
		int[] pixels = null;
		try {
			BufferedImage img = ImageIO.read(new FileInputStream(path));
			// BufferedImage img = ImageIO.read(new FileInputStream(path));
			width = img.getWidth();
			height = img.getHeight();
			pixels = new int[width * height];
			img.getRGB(0, 0, width, height, pixels, 0, width);
			System.out.println("Texture loaded: " + path);
		} catch (IOException e) {
			System.err.println("Texture loading error: " + path);
			e.printStackTrace();
		}

		int[] data = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			int a = (pixels[i] & 0xff000000) >> 24;
			int r = (pixels[i] & 0xff0000) >> 16;
			int g = (pixels[i] & 0xff00) >> 8;
			int b = (pixels[i] & 0xff);
			data[i] = a << 24 | b << 16 | g << 8 | r;
		}

		int tex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, tex);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createIntBuffer(data));
		glBindTexture(GL_TEXTURE_2D, 0);
		return tex;
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, ID);
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getID() {
		return ID;
	}

	public static int[] loadFont(String path, int hLength, int vLength, int size) {
		int width = 0;
		int height = 0;
		int index = 0;
		int[] ids = new int[hLength * vLength];
		int[] sheet = null;
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(path));
			width = image.getWidth();
			height = image.getHeight();
			sheet = new int[width * height];
			image.getRGB(0, 0, width, height, sheet, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int y0 = 0; y0 < vLength; y0++) {
			for (int x0 = 0; x0 < hLength; x0++) {
				int[] letter = new int[size * size];
				for (int y = 0; y < size; y++) {
					for (int x = 0; x < size; x++) {
						letter[x + y * size] = sheet[(x + x0 * size) + (y + y0 * size) * width];
					}
				}
				ByteBuffer buffer = org.lwjgl.BufferUtils.createByteBuffer(size * size * 4);
				for (int y = 0; y < size; y++) {
					for (int x = 0; x < size; x++) {
						byte a = (byte) ((letter[x + y * size] & 0xff000000) >> 24);
						byte r = (byte) ((letter[x + y * size] & 0xff0000) >> 16);
						byte g = (byte) ((letter[x + y * size] & 0xff00) >> 8);
						byte b = (byte) (letter[x + y * size] & 0xff);
						buffer.put(r).put(g).put(b).put(a);
					}
				}
				buffer.flip();
				int texID = glGenTextures();
				glBindTexture(GL_TEXTURE_2D, texID);
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, size, size, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				textures.add(texID);
				ids[index++] = texID;
				glBindTexture(GL_TEXTURE_2D, 0);

			}
		}
		return ids;
	}

}

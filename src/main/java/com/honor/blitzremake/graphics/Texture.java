package com.honor.blitzremake.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

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

	/**
	 * Opens a resource on the classpath. The {@code res/} prefix used by the
	 * static field initializers is stripped — resources live at
	 * {@code src/main/resources/} so on the classpath the path is e.g.
	 * {@code img/blast1.png}.
	 */
	private static InputStream openClasspath(String path) {
		String cp = path.startsWith("res/") ? path.substring(4) : path;
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(cp);
		if (in == null) {
			System.err.println("Texture resource not found on classpath: " + cp);
		}
		return in;
	}

	/** Reads an {@link InputStream} fully into a direct {@link ByteBuffer}. */
	private static ByteBuffer readToByteBuffer(InputStream in) throws IOException {
		java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(8192);
		byte[] buf = new byte[8192];
		int n;
		while ((n = in.read(buf)) != -1) {
			out.write(buf, 0, n);
		}
		byte[] bytes = out.toByteArray();
		ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
		buffer.put(bytes).flip();
		return buffer;
	}

	private int load(String path) {
		try (InputStream in = openClasspath(path)) {
			if (in == null) {
				return 0;
			}
			ByteBuffer encoded = readToByteBuffer(in);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer w = stack.mallocInt(1);
				IntBuffer h = stack.mallocInt(1);
				IntBuffer channels = stack.mallocInt(1);
				ByteBuffer pixels = stbi_load_from_memory(encoded, w, h, channels, STBI_rgb_alpha);
				if (pixels == null) {
					System.err.println("STB failed to decode " + path + ": " + stbi_failure_reason());
					MemoryUtil.memFree(encoded);
					return 0;
				}
				width = w.get(0);
				height = h.get(0);

				int tex = glGenTextures();
				glBindTexture(GL_TEXTURE_2D, tex);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
				glBindTexture(GL_TEXTURE_2D, 0);

				stbi_image_free(pixels);
				MemoryUtil.memFree(encoded);
				System.out.println("Texture loaded: " + path);
				return tex;
			}
		} catch (IOException e) {
			System.err.println("Texture loading error: " + path);
			e.printStackTrace();
		}
		return 0;
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

	/**
	 * Loads a glyph-atlas PNG from the classpath and slices it into per-glyph
	 * GL textures. Decodes via STB (AWT-free); the atlas layout is a grid of
	 * {@code hLength} x {@code vLength} cells, each {@code size} x
	 * {@code size} pixels.
	 */
	public static int[] loadFont(String path, int hLength, int vLength, int size) {
		int index = 0;
		int[] ids = new int[hLength * vLength];
		try (InputStream in = openClasspath(path)) {
			if (in == null) {
				return ids;
			}
			ByteBuffer encoded = readToByteBuffer(in);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer w = stack.mallocInt(1);
				IntBuffer h = stack.mallocInt(1);
				IntBuffer channels = stack.mallocInt(1);
				ByteBuffer atlas = stbi_load_from_memory(encoded, w, h, channels, STBI_rgb_alpha);
				if (atlas == null) {
					System.err.println("STB failed to decode font " + path + ": " + stbi_failure_reason());
					MemoryUtil.memFree(encoded);
					return ids;
				}
				int atlasW = w.get(0);
				int atlasH = h.get(0);

				ByteBuffer glyph = MemoryUtil.memAlloc(size * size * 4);
				try {
					for (int y0 = 0; y0 < vLength; y0++) {
						for (int x0 = 0; x0 < hLength; x0++) {
							// Extract one size x size cell from the atlas into
							// a contiguous buffer (RGBA, top-left origin).
							glyph.clear();
							for (int y = 0; y < size; y++) {
								int rowBase = ((y0 * size) + y) * atlasW;
								int colBase = (x0 * size);
								for (int x = 0; x < size; x++) {
									int srcIdx = (rowBase + colBase + x) * 4;
									glyph
											.put(atlas.get(srcIdx))
											.put(atlas.get(srcIdx + 1))
											.put(atlas.get(srcIdx + 2))
											.put(atlas.get(srcIdx + 3));
								}
							}
							glyph.flip();

							int texID = glGenTextures();
							glBindTexture(GL_TEXTURE_2D, texID);
							glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, size, size, 0, GL_RGBA, GL_UNSIGNED_BYTE, glyph);
							glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
							glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
							textures.add(texID);
							ids[index++] = texID;
							glBindTexture(GL_TEXTURE_2D, 0);
						}
					}
				} finally {
					MemoryUtil.memFree(glyph);
				}
				stbi_image_free(atlas);
			}
			MemoryUtil.memFree(encoded);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ids;
	}

}
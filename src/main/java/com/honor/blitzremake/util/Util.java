package com.honor.blitzremake.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.honor.blitzremake.graphics.Window;

public class Util {

	private static final Path CONFIG_PATH = Path.of("config.json");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static float Clamp(float value, float min, float max) {
		if (value > max)
			return max;
		if (value < min)
			return min;
		return value;
	}

	public static int abs(float value) {
		if (value > 0)
			return 1;
		if (value < 0)
			return -1;
		return 0;
	}

	/**
	 * Hides the OS cursor over the game window. Replaces the LWJGL 2
	 * {@code Mouse.setNativeCursor(emptyCursor)} hack with GLFW's
	 * {@code GLFW_CURSOR_HIDDEN} input mode.
	 */
	public static void setBlankCursor() {
		Window.hideCursor();
	}

	/**
	 * Loads the persisted game settings from {@code config.json}. Falls back
	 * to {@link Config#defaults()} (and logs) when the file is missing or
	 * unreadable, so a fresh install launches without error.
	 */
	public static Config loadConfig() {
		if (!Files.exists(CONFIG_PATH)) {
			System.out.println("config.json not found; using defaults.");
			return Config.defaults();
		}
		try {
			String json = Files.readString(CONFIG_PATH);
			Config cfg = GSON.fromJson(json, Config.class);
			if (cfg == null) {
				return Config.defaults();
			}
			return cfg;
		} catch (IOException e) {
			System.err.println("Could not read config.json; using defaults.");
			e.printStackTrace();
			return Config.defaults();
		}
	}

	/**
	 * Persists the given settings to {@code config.json} (pretty-printed).
	 */
	public static void saveConfig(Config config) {
		try {
			Files.writeString(CONFIG_PATH, GSON.toJson(config));
		} catch (IOException e) {
			System.err.println("Could not write config.json.");
			e.printStackTrace();
		}
	}

}
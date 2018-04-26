package com.honor.blitzremake;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import java.util.Random;
import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import com.honor.blitzremake.entity.MyCursor;
import com.honor.blitzremake.entity.Player;
import com.honor.blitzremake.font.Font;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.hud.HUD;
import com.honor.blitzremake.input.Input;
import com.honor.blitzremake.level.Level;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.menu.Ending;
import com.honor.blitzremake.menu.Menu;
import com.honor.blitzremake.sound.Sound;
import com.honor.blitzremake.util.MyDisplay;
import com.honor.blitzremake.util.Util;

public class Game implements Runnable {

	public static final int WIDTH = 900;
	public static final int HEIGHT = WIDTH / 16 * 9;
	public static String title = "Blitz Remake Project final version";
	public static Random rnd = new Random();
	public static float time;

	private Thread thread;
	private boolean running = false;
	private Menu menu;
	private Level level;
	private Ending ending;

	public static void main(String[] args) {
		new Game().start();
	}

	public void start() {
		running = true;
		thread = new Thread(this, "BlitzRemake");
		thread.start();
	}

	private void initGL() {
		Display.setVSyncEnabled(true);
		String version = glGetString(GL_VERSION);
		System.out.println("OpenGL " + version);

		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0f, 0f, 0f, 1.0f);
		glActiveTexture(GL_TEXTURE0);
	}

	private void loadResources() {
		Resources.loadMeshes();
		Shader.loadAll();

		Matrix4f pr_matrix = Matrix4f.orthographic(0.0f, WIDTH, HEIGHT, 0.0f, -1.0f, 1.0f);

		Shader.MENU.enable();
		Shader.MENU.setUniformMatrix("pr_matrix", pr_matrix);
		Shader.MENU.setUniform1i("tex", 0);
		Shader.MENU.disable();

		Shader.LEVEL.enable();
		Shader.LEVEL.setUniformMatrix("pr_matrix", pr_matrix);
		Shader.LEVEL.setUniform1i("tex", 0);
		Shader.LEVEL.disable();

		Shader.HEALTH.enable();
		Shader.HEALTH.setUniformMatrix("pr_matrix", pr_matrix);
		Shader.HEALTH.setUniform1i("tex", 0);
		Shader.HEALTH.disable();

		Shader.BASIC.enable();
		Shader.BASIC.setUniformMatrix("pr_matrix", pr_matrix);
		Shader.BASIC.setUniform1i("tex", 0);
		Shader.BASIC.disable();

		Shader.PROJECTILE.enable();
		Shader.PROJECTILE.setUniformMatrix("pr_matrix", pr_matrix);
		Shader.PROJECTILE.setUniform1i("tex", 0);
		Shader.PROJECTILE.disable();

		Shader.LIGHT.enable();
		Shader.LIGHT.setUniformMatrix("pr_matrix", pr_matrix);
		Shader.LIGHT.disable();

		Shader.PARTICLE.enable();
		Shader.PARTICLE.setUniformMatrix("pr_matrix", pr_matrix);
		Shader.PARTICLE.setUniform1i("tex", 0);
		Shader.PARTICLE.disable();

		Shader.SPARKLE.enable();
		Shader.SPARKLE.setUniformMatrix("pr_matrix", pr_matrix);
		Shader.SPARKLE.setUniform1i("tex", 0);
		Shader.SPARKLE.disable();

		Shader.FONT.enable();
		Shader.FONT.setUniformMatrix("pr_matrix", pr_matrix);
		Shader.FONT.setUniform1i("tex", 0);
		Shader.FONT.disable();

		Shader.BLOODSPLAT.enable();
		Shader.BLOODSPLAT.setUniformMatrix("pr_matrix", pr_matrix);
		Shader.BLOODSPLAT.setUniform1i("tex", 0);
		Shader.BLOODSPLAT.disable();

		Font.load();
		Sound.loadAll();

	}

	public void run() {
		int windowed = Util.getProperty("Windowed");
		int w = Util.getProperty("Width");
		int h = Util.getProperty("Height");
		if(windowed == 1)
			MyDisplay.set();
		else
			MyDisplay.setDisplayMode(w, h, true);
		
		Util.setBlankCursor();
		initGL();
		Input.create();
		loadResources();
				
		menu = new Menu();
		level = new Level();
		ending = new Ending(this);
		
		Sound.theme1.playAsSoundEffect(1.0f, 0.5f, true);

		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60.0;
		double delta_ups = 0;
		int frames = 0;
		int updates = 0;
		while (running) {
			long now = System.nanoTime();
			delta_ups += (now - lastTime) / ns;
			lastTime = now;
			while (delta_ups >= 1) {
				update(); // LOGIC: 60 times/sec
				updates++;
				delta_ups--;
			}
			render(); // GRAPHIC
			frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				// System.out.println(updates + " ups, " + frames + " fps");
				Display.setTitle(title + " | " + updates + " ups, " + frames + " fps");
				updates = 0;
				frames = 0;
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				running = false;
			}

			if (Display.isCloseRequested()) {
				running = false;
			}
		}

		Display.destroy();
		AL.destroy();
	}

	private void update() {
		time %= 24000;
		Input.pollInput();
		if (State.getState() == State.MENU) {
			menu.update();
		}
		if (State.getState() == State.GAME) {
			level.update();
			MyCursor.update();
		}
		if (State.getState() == State.ENDING) {
			ending.update();
		}
		
		time++;
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		if (State.getState() == State.MENU) {
			menu.render();
		}
		if (State.getState() == State.GAME) {
			level.render();
			MyCursor.render();
		}
		if (State.getState() == State.ENDING) {
			ending.render();
		}
		Display.update();
	}

	public void reset() {
		level = new Level(); 
		Level.resettingStaticVariables();
		Player.ammo = 0;
		Player.failed = false;
		Player.hasWeapon = false;
		Player.pickedUpPowerCells = 0;
		Player.health = 10;
		HUD.resetTime();
		State.setState(State.GAME);
	}

}

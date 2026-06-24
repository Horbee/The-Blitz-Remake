package com.honor.blitzremake.level;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;

import com.honor.blitzremake.Game;
import com.honor.blitzremake.Resources;
import com.honor.blitzremake.State;
import com.honor.blitzremake.camera.Camera;
import com.honor.blitzremake.entity.Enemy;
import com.honor.blitzremake.entity.Player;
import com.honor.blitzremake.entity.Entity;
import com.honor.blitzremake.entity.Projectile;
import com.honor.blitzremake.entity.SpawnerPipe;
import com.honor.blitzremake.entity.others.Collider;
import com.honor.blitzremake.entity.others.Door;
import com.honor.blitzremake.entity.others.Elevator;
import com.honor.blitzremake.entity.others.Generator;
import com.honor.blitzremake.entity.others.SmokeGrid;
import com.honor.blitzremake.entity.particle.AmmoFXEmitter;
import com.honor.blitzremake.entity.particle.BloodEmitter;
import com.honor.blitzremake.entity.particle.BloodSplat;
import com.honor.blitzremake.entity.particle.BlowFXEmitter;
import com.honor.blitzremake.entity.particle.SmokeFXEmitter;
import com.honor.blitzremake.entity.particle.SparkleEmitter;
import com.honor.blitzremake.entity.pickups.Ammo;
import com.honor.blitzremake.entity.pickups.PowerCell;
import com.honor.blitzremake.entity.pickups.Weapon;
import com.honor.blitzremake.font.Font;
import com.honor.blitzremake.graphics.Fader;
import com.honor.blitzremake.graphics.Light;
import com.honor.blitzremake.graphics.Renderer;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.graphics.Texture;
import com.honor.blitzremake.hud.HUD;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;
import com.honor.blitzremake.sound.Sound;

public class Level {

	public static int phase = 0;

	public List<Projectile> projectiles = new ArrayList<Projectile>();
	public List<Ammo> ammos = new ArrayList<Ammo>();
	public List<PowerCell> powercells = new ArrayList<PowerCell>();
	public List<Wall> walls = new ArrayList<Wall>();
	public List<Collider> colliders = new ArrayList<Collider>();
	public List<Enemy> enemys = new ArrayList<Enemy>();
	public List<SparkleEmitter> sparkle_emitters = new ArrayList<SparkleEmitter>();
	public List<BloodEmitter> blood_emitters = new ArrayList<BloodEmitter>();

	private Renderer renderer;
	public Player player;
	public Elevator elevator;
	public Door door;
	public SpawnerPipe pipe1, pipe2, pipe3, pipe4;
	public static int xOffset, yOffset;
	private Random rnd = new Random();
	private int time;
	public static boolean levelEnd = false;

	Weapon w;
	public Generator g;
	SmokeGrid sg;

	public Fader fader;

	public Level() {
		renderer = new Renderer();

		// Player starts in the elevator
		player = new Player(163, 1742);
		player.init(this);

		add(new Collider(-70, 1615, 30, 447));
		add(new Collider(145, 1400, 430, 30));
		add(new Collider(145, 1830, 430, 30));
		add(new Collider(565, 1730, 410, 30));
		add(new Collider(770, 1515, 30, 447));
		add(new Collider(540, 1390, 30, 200));
		add(new Collider(365, 1450, 30, 110));
		add(new Collider(365, 1780, 30, 110));

		add(new Collider(455, 1500, 190, 30));
		add(new Collider(255, -10, 560, 30));
		add(new Collider(1015, -10, 560, 30));
		add(new Collider(255, 1295, 560, 30));
		add(new Collider(1035, 1295, 560, 30));

		add(new Collider(-15, 652, 30, 1290));
		add(new Collider(1300, 652, 30, 1290));

		sg = new SmokeGrid(640, 630);

		door = new Door(650, 1310);
		door.init(this);
		elevator = new Elevator(640, -100);
		elevator.init(this);
		pipe1 = new SpawnerPipe(0, 0, 45);
		pipe2 = new SpawnerPipe(0, 256 * 5, -45);
		pipe3 = new SpawnerPipe(256 * 5, 0, 135);
		pipe4 = new SpawnerPipe(256 * 5, 256 * 5, -135);

		placeItems();

		add(new Ammo(270, 1480));

		w = new Weapon(50, 1480);
		add(w);
		g = new Generator(655, 1652);
		add(g);

		fader = new Fader(false);
	}

	private void placeItems() {
		// Creating rectangle Walls
		add(new Wall(384, 384));
		add(new Wall(896, 384));
		add(new Wall(384, 896));
		add(new Wall(896, 896));

		// Creating PowerCells (8)
		add(new PowerCell(178, 239));
		add(new PowerCell(178, 614));
		add(new PowerCell(178, 1025));

		add(new PowerCell(646, 461));
		add(new PowerCell(646, 803));

		add(new PowerCell(1072, 239));
		add(new PowerCell(1072, 614));
		add(new PowerCell(1072, 1025));

		// Creating Ammo
		add(new Ammo(640, 644));
		add(new Ammo(886, 161));
		add(new Ammo(412, 161));
		add(new Ammo(349, 1145));
		add(new Ammo(1000, 1145));
	}

	public void update() {

		if (!Sound.waterfall.isPlaying())
			Sound.waterfall.playAsSoundEffect(1f, 1f, false);

		player.update();

		pipe1.update();
		pipe2.update();
		pipe3.update();
		pipe4.update();

		elevator.update();

		if (g.destroyed)
			door.update();

		for (int i = 0; i < sparkle_emitters.size(); i++) {
			if (sparkle_emitters.get(i).shouldRemove) {
				sparkle_emitters.remove(i);
			} else {
				sparkle_emitters.get(i).update();
			}
		}

		for (int i = 0; i < blood_emitters.size(); i++) {
			if (blood_emitters.get(i).shouldRemove) {
				blood_emitters.remove(i);
			} else {
				blood_emitters.get(i).update();
			}
		}

		BloodSplat.updateBlood();

		for (int i = 0; i < enemys.size(); i++) {
			if (enemys.get(i).isRemoved) {
				enemys.remove(i);
			} else {
				if (!Level.levelEnd)
					enemys.get(i).update();
			}
		}

		if (time % 300 == 0) {
			if (enemys.size() < 12)
				spawnEnemy();
		}

		for (int i = 0; i < projectiles.size(); i++) {
			if (projectiles.get(i).isRemoved) {
				projectiles.remove(i);
			} else {
				projectiles.get(i).update();
			}
		}

		for (int i = 0; i < ammos.size(); i++) {
			if (ammos.get(i).isRemoved) {
				ammos.remove(i);
			} else {
				ammos.get(i).update();
			}
		}
		AmmoFXEmitter.updateEmitters();

		SmokeFXEmitter.updateEmitters();

		BlowFXEmitter.updateEmitters();

		for (int i = 0; i < powercells.size(); i++) {
			if (powercells.get(i).isRemoved) {
				powercells.remove(i);
			} else {
				powercells.get(i).update();
			}
		}

		if (!w.isRemoved)
			w.update();

		g.update();

		HUD.update();

		time++;

		fader.update();
	}

	public void render() {

		Camera.move(-xOffset, -yOffset);

		Shader.LEVEL.enable();
		Texture.ground.bind();
		Resources.ground_mesh.bind(Shader.LEVEL);
		Shader.LEVEL.setUniformMatrix("vw_matrix", Camera.getViewMatrix());
		Shader.LEVEL.setUniform2f("player", (player.position.x - xOffset), (player.position.y - yOffset));

		for (int height = 0; height < 5; height++) {
			for (int width = 0; width < 5; width++) {
				Shader.LEVEL.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(width * 256.0f + 128.0f, height * 256.0f + 128.0f, 0.0f)));
				Resources.ground_mesh.draw();
			}
		}
		Texture.ground.unbind();

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		Texture.preLevel.bind();
		Resources.preLevel_mesh.bind(Shader.LEVEL);
		Shader.LEVEL.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(350f, 1580f, 0.0f)).multiply(Matrix4f.scale(new Vector3f(1f, -1f, 1f))));
		Resources.preLevel_mesh.draw();

		Texture.decal1.bind();
		Resources.decal1_mesh.bind(Shader.LEVEL);
		Shader.LEVEL.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(157f, 1754f, 0.0f)).multiply(Matrix4f.scale(new Vector3f(1f, -1f, 1f))));
		Resources.decal1_mesh.draw();

		glDisable(GL_BLEND);

		// WALL
		for (int i = 0; i < walls.size(); i++) {
			renderer.addEntity(walls.get(i));
			// walls.get(i).render();
		}

		// collider OLNY FOR DEBUGGING
		/*
		 * for (int i = 0; i < colliders.size(); i++) {
		 * renderer.addEntity(colliders.get(i)); }
		 */

		// BOUNDING WALL
		Texture.wall2.bind();
		Shader.LEVEL.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(-16, 640, 0)).multiply(Matrix4f.scale(new Vector3f(1, 40, 1))));
		Resources.hp_mesh.render(Shader.LEVEL);
		Shader.LEVEL.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(256 * 5 + 15, 640, 0)).multiply(Matrix4f.scale(new Vector3f(1, 40, 1))));
		Resources.hp_mesh.render(Shader.LEVEL);
		Shader.LEVEL.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(640, 256 * 5 + 15, 0)).multiply(Matrix4f.scale(new Vector3f(42, 1, 1))));
		Resources.hp_mesh.render(Shader.LEVEL);
		Shader.LEVEL.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(210, -16, 0)).multiply(Matrix4f.scale(new Vector3f(15, 1, 1))));
		Resources.hp_mesh.render(Shader.LEVEL);
		Shader.LEVEL.setUniformMatrix("ml_matrix", Matrix4f.translate(new Vector3f(1010, -16, 0)).multiply(Matrix4f.scale(new Vector3f(19, 1, 1))));
		Resources.hp_mesh.render(Shader.LEVEL);
		Texture.wall2.unbind();

		// ELEVATOR
		elevator.render();

		// DOOR render
		door.render();

		// SomkeGrid
		sg.render();

		Shader.LEVEL.disable();

		for (int i = 0; i < sparkle_emitters.size(); i++) {
			sparkle_emitters.get(i).renderParticles();
		}

		for (int i = 0; i < blood_emitters.size(); i++) {
			blood_emitters.get(i).renderParticles();
		}

		BloodSplat.renderBlood();

		for (int i = 0; i < enemys.size(); i++) {
			renderer.addEntity(enemys.get(i));
		}

		for (int i = 0; i < ammos.size(); i++) {
			renderer.addEntity(ammos.get(i));
			renderer.addLight(ammos.get(i).getLight());
		}
		AmmoFXEmitter.renderFX();
		SmokeFXEmitter.renderFX();
		BlowFXEmitter.renderFX();

		for (int i = 0; i < powercells.size(); i++) {
			renderer.addEntity(powercells.get(i));
			renderer.addLight(powercells.get(i).getLight());
		}

		if (!w.isRemoved) {
			renderer.addEntity(w);
			renderer.addLight(w.getLight());
		}

		renderer.addEntity(g);
		renderer.addLight(g.getLight());

		for (int i = 0; i < projectiles.size(); i++) {
			renderer.addEntity(projectiles.get(i));
			renderer.addLight(projectiles.get(i).getLight());
		}

		player.render();

		// PIPES
		renderer.addEntity(pipe1);
		pipe1.renderGoo();
		renderer.addEntity(pipe2);
		pipe2.renderGoo();
		renderer.addEntity(pipe3);
		pipe3.renderGoo();
		renderer.addEntity(pipe4);
		pipe4.renderGoo();

		renderer.render();

		HUD.render();

		fader.render();

		if (levelEnd) {
			if (!runOnce) {
				runOnce = true;
				fader.reset(true);
				time = 1;
			}
			if (time % 120 == 0)
				State.setState(State.ENDING);
			if (!Player.failed)
				Font.drawString("CONGRATULATIONS YOU WON!", Game.WIDTH / 2 - 290, 100, 15, -10, new Vector3f(1, 0f, .25f));
		}

		if (Player.failed) {
			Font.drawString("CONGRATULATIONS YOU HAVE DIED!", Game.WIDTH / 2 - 330, 100, 15, -10, new Vector3f(1, 0f, .25f));
		}

	}

	boolean runOnce;

	private void spawnEnemy() {
		int cube = rnd.nextInt(4);
		if (cube == 0)
			add(new Enemy(50, 50));
		if (cube == 1)
			add(new Enemy(50, 1200));
		if (cube == 2)
			add(new Enemy(1200, 50));
		if (cube == 3)
			add(new Enemy(1200, 1200));

	}

	public void add(Entity e) {
		e.init(this);
		if (e instanceof Projectile) {
			projectiles.add((Projectile) e);
		} else if (e instanceof Ammo) {
			ammos.add((Ammo) e);
		} else if (e instanceof PowerCell) {
			powercells.add((PowerCell) e);
		} else if (e instanceof Wall) {
			walls.add((Wall) e);
		} else if (e instanceof Collider) {
			colliders.add((Collider) e);
		} else if (e instanceof Enemy) {
			enemys.add((Enemy) e);
		}
	}

	public void setEnemysAggro() {
		for (int i = 0; i < enemys.size(); i++) {
			enemys.get(i).max_distance = 100000;
		}
	}

	public void end() {
		if (!levelEnd)
			time = 0;
		levelEnd = true;
	}

	public void setOffset(int xOffset, int yOffset) {
		Level.xOffset = xOffset;
		Level.yOffset = yOffset;
	}

	public int getxOffset() {
		return xOffset;
	}

	public int getyOffset() {
		return yOffset;
	}

	public static void resettingStaticVariables() {
		levelEnd = false;
		phase = 0;
	}

}

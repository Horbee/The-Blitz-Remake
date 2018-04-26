package com.honor.blitzremake.sound;

import java.io.IOException;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Sound {

	public static Audio gun, pickup1, pickup2, pickup3, walk1, walk2, hitWall, bugHit1, bugHit2, bugDie, explosion, door, waterfall;
	public static Audio theme1;
	
	public static void loadAll() {
		try {
			theme1 = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/theme1.ogg"));
			gun = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/gun.ogg"));
			pickup1 = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/pickup1.ogg"));
			pickup2 = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/pickup2.ogg"));
			pickup3 = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/pickup3.ogg"));
			walk1 = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/walk1.ogg"));
			walk2 = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/walk2.ogg"));
			hitWall = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/hitWall.ogg"));
			bugHit1 = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/bugHit1.ogg"));
			bugHit2 = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/bugHit2.ogg"));
			bugDie = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/bugDie.ogg"));
			explosion = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/explosion.ogg"));
			door = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/mechanicalDoor.ogg"));
			waterfall = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/waterfall.ogg"));
		} catch (IOException e) {
			System.err.println("Cannot Load Sound!");
			e.printStackTrace();
		}
	}

}

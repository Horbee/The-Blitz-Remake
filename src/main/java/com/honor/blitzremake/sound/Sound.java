package com.honor.blitzremake.sound;

/**
 * Game sound registry. The static {@code Audio} fields preserve the 2015
 * call sites ({@code Sound.gun.playAsSoundEffect(...)}, etc.).
 *
 * In Step 1.3 the loader is a no-op stub because Slick-Util (which provided
 * {@code AudioLoader.getAudio("OGG", ...)}) is incompatible with LWJGL 3 and
 * was removed from the classpath. Step 1.6 will reimplement {@link #loadAll}
 * using STB vorbis to decode the OGG files in {@code resources/sounds/} into
 * OpenAL buffers/sources.
 */
public class Sound {

	public static Audio gun, pickup1, pickup2, pickup3, walk1, walk2, hitWall, bugHit1, bugHit2, bugDie, explosion, door, waterfall;
	public static Audio theme1;

	public static void loadAll() {
		// TODO Step 1.6: decode OGG files via stb_vorbis and create OpenAL
		// buffers/sources. For now all clips are no-ops so the game runs silent.
		Audio stub = new NoOpAudio();
		theme1 = stub;
		gun = stub;
		pickup1 = stub;
		pickup2 = stub;
		pickup3 = stub;
		walk1 = stub;
		walk2 = stub;
		hitWall = stub;
		bugHit1 = stub;
		bugHit2 = stub;
		bugDie = stub;
		explosion = stub;
		door = stub;
		waterfall = stub;
	}

}
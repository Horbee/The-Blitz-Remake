package com.honor.blitzremake.sound;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * Game sound registry. The static {@code Audio} fields preserve the 2015
 * call sites ({@code Sound.gun.playAsSoundEffect(...)}, etc.).
 *
 * Step 1.6: {@link #loadAll} decodes each OGG in {@code sounds/} via
 * {@code stb_vorbis_decode_memory} into an OpenAL buffer and wraps it in
 * an {@link OpenALAudio} clip. {@link #dispose()} frees all clips and is
 * called by {@link com.honor.blitzremake.Game} before the OpenAL context
 * is destroyed.
 */
public class Sound {

	public static Audio gun, pickup1, pickup2, pickup3, walk1, walk2, hitWall, bugHit1, bugHit2, bugDie, explosion, door, waterfall;
	public static Audio theme1;

	private static final java.util.List<OpenALAudio> clips = new java.util.ArrayList<>();

	public static void loadAll() {
		gun        = load("sounds/gun.ogg");
		pickup1    = load("sounds/pickup1.ogg");
		pickup2    = load("sounds/pickup2.ogg");
		pickup3    = load("sounds/pickup3.ogg");
		walk1      = load("sounds/walk1.ogg");
		walk2      = load("sounds/walk2.ogg");
		hitWall    = load("sounds/hitWall.ogg");
		bugHit1    = load("sounds/bugHit1.ogg");
		bugHit2    = load("sounds/bugHit2.ogg");
		bugDie     = load("sounds/bugDie.ogg");
		explosion  = load("sounds/explosion.ogg");
		door       = load("sounds/mechanicalDoor.ogg");
		waterfall  = load("sounds/waterfall.ogg");
		theme1     = load("sounds/theme1.ogg");
	}

	/**
	 * Decodes one OGG from the classpath into an OpenAL buffer and returns
	 * an {@link OpenALAudio} clip. Returns a {@link NoOpAudio} stub on any
	 * failure so the game runs (silent for that clip) instead of crashing.
	 */
	private static Audio load(String classpathPath) {
		try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(classpathPath)) {
			if (in == null) {
				System.err.println("Sound resource not found on classpath: " + classpathPath);
				return new NoOpAudio();
			}
			ByteBuffer encoded = readToByteBuffer(in);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer channels = stack.mallocInt(1);
				IntBuffer sampleRate = stack.mallocInt(1);
				ShortBuffer pcm = stb_vorbis_decode_memory(encoded, channels, sampleRate);
				if (pcm == null) {
					System.err.println("STB vorbis failed to decode: " + classpathPath);
					MemoryUtil.memFree(encoded);
					return new NoOpAudio();
				}
				int format = (channels.get(0) == 2) ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16;
				int buffer = alGenBuffers();
				alBufferData(buffer, format, pcm, sampleRate.get(0));
				// stb_vorbis_decode_memory returns a buffer allocated by STB
				// that we must free; OpenAL copies the data into its own
				// storage during alBufferData.
				MemoryUtil.memFree(pcm);
				MemoryUtil.memFree(encoded);

				OpenALAudio clip = new OpenALAudio(buffer);
				clips.add(clip);
				System.out.println("Sound loaded: " + classpathPath);
				return clip;
			}
		} catch (IOException e) {
			System.err.println("Could not load sound " + classpathPath);
			e.printStackTrace();
			return new NoOpAudio();
		}
	}

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

	/** Frees every OpenAL buffer + source. Call before destroying the AL context. */
	public static void dispose() {
		for (OpenALAudio clip : clips) {
			clip.dispose();
		}
		clips.clear();
	}

}
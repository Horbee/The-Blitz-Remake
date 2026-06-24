package com.honor.blitzremake.sound;

/**
 * Stub audio clip interface preserving the Slick-Util {@code Audio} surface
 * ({@code playAsSoundEffect}, {@code isPlaying}, {@code stop}) so the 15
 * call sites across the game compile during the LWJGL 2 -> 3 port.
 *
 * All methods are no-ops in Step 1.3; the real implementation backed by
 * STB vorbis + OpenAL sources lands in Step 1.6.
 */
public interface Audio {

    void playAsSoundEffect(float pitch, float volume, boolean loop);

    boolean isPlaying();

    void stop();
}
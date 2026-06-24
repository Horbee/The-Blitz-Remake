package com.honor.blitzremake.sound;

import static org.lwjgl.openal.AL10.*;

import java.nio.ShortBuffer;

import org.lwjgl.system.MemoryUtil;

/**
 * {@link Audio} clip backed by an OpenAL buffer + a small pool of sources
 * so overlapping play calls (e.g. rapid gunshots, alternating footsteps)
 * don't cut each other off. Replaces the Step 1.3 {@link NoOpAudio} stub.
 *
 * <p>A clip owns one buffer (the decoded PCM) and {@code POOL_SIZE}
 * sources. Each {@link #playAsSoundEffect} call round-robins to the next
 * source and restarts it; {@link #stop} stops all sources; {@link
 * #isPlaying} reports whether any source is currently playing.
 *
 * <p>Buffers and sources are freed in {@link #dispose()}, which
 * {@link Sound#dispose()} calls during shutdown.
 */
public class OpenALAudio implements Audio {

    private static final int POOL_SIZE = 4;

    private final int buffer;
    private final int[] sources = new int[POOL_SIZE];
    private int nextSource = 0;

    /**
     * @param buffer     OpenAL buffer id (PCM already uploaded via
     *                   {@code alBufferData})
     */
    public OpenALAudio(int buffer) {
        this.buffer = buffer;
        for (int i = 0; i < POOL_SIZE; i++) {
            int src = alGenSources();
            alSourcei(src, AL_BUFFER, buffer);
            sources[i] = src;
        }
    }

    @Override
    public void playAsSoundEffect(float pitch, float volume, boolean loop) {
        int src = sources[nextSource];
        nextSource = (nextSource + 1) % POOL_SIZE;
        alSourceStop(src);
        alSourcef(src, AL_PITCH, pitch);
        alSourcef(src, AL_GAIN, volume);
        alSourcei(src, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
        alSourcePlay(src);
    }

    @Override
    public boolean isPlaying() {
        for (int src : sources) {
            if (alGetSourcei(src, AL_SOURCE_STATE) == AL_PLAYING) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void stop() {
        for (int src : sources) {
            alSourceStop(src);
        }
    }

    /** Frees the OpenAL sources and the buffer. */
    public void dispose() {
        for (int src : sources) {
            alSourceStop(src);
            alSourcei(src, AL_BUFFER, 0);
            alDeleteSources(src);
        }
        alDeleteBuffers(buffer);
    }
}
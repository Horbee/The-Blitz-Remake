package com.honor.blitzremake.sound;

/**
 * No-op {@link Audio} clip. Returned by {@link Sound#loadAll} during the
 * platform-layer port (Step 1.3). Step 1.6 replaces this with a real OpenAL
 * source fed by STB vorbis-decoded OGG data.
 */
final class NoOpAudio implements Audio {

    @Override
    public void playAsSoundEffect(float pitch, float volume, boolean loop) {
        // no-op: audio implementation lands in Step 1.6
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public void stop() {
        // no-op
    }
}
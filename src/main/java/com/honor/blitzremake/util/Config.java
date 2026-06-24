package com.honor.blitzremake.util;

/**
 * Persisted game settings, serialised to {@code config.json} by
 * {@link Util#loadConfig()} / {@link Util#saveConfig(Config)} via Gson.
 *
 * @param windowed  true for a windowed window, false for borderless fullscreen
 * @param width     framebuffer width (ignored in windowed mode, which always
 *                  uses {@code Game.WIDTH} x {@code Game.HEIGHT})
 * @param height    framebuffer height
 */
public record Config(boolean windowed, int width, int height) {

    /** Fallback used when {@code config.json} is missing or unreadable. */
    public static Config defaults() {
        return new Config(true, 900, 506);
    }
}
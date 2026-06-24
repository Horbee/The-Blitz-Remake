package com.honor.blitzremake.util;

import com.honor.blitzremake.Game;
import com.honor.blitzremake.graphics.Window;

/**
 * Window setup helper. Thin wrapper over {@link Window#init} preserving the
 * 2015 call sites ({@code MyDisplay.set()} / {@code MyDisplay.setDisplayMode}).
 *
 * Replaces the LWJGL 2 {@code org.lwjgl.opengl.Display}/{@code DisplayMode}
 * API: GLFW has no separate "display mode" object, so windowed mode just
 * creates a window of the given size and fullscreen passes the primary
 * monitor to {@code glfwCreateWindow}.
 */
public final class MyDisplay {

    private MyDisplay() {
    }

    /** Windowed at the default 900x506 resolution. */
    public static void set() {
        Window.init(Game.WIDTH, Game.HEIGHT, false);
    }

    /**
     * Windowed or fullscreen at the given resolution.
     *
     * @param width     framebuffer width
     * @param height    framebuffer height
     * @param fullscreen whether to borderless-fullscreen on the primary monitor
     */
    public static void setDisplayMode(int width, int height, boolean fullscreen) {
        Window.init(width, height, fullscreen);
    }
}
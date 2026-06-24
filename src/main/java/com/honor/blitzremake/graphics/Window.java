package com.honor.blitzremake.graphics;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import com.honor.blitzremake.Game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

/**
 * Owns the GLFW window handle and the current framebuffer size.
 *
 * Replaces the LWJGL 2 {@code org.lwjgl.opengl.Display} surface. Created once
 * at startup; input callbacks are registered here and feed the {@link
 * com.honor.blitzremake.input.Input} façade so game logic above stays
 * unchanged from the 2015 build.
 *
 * Window size is cached so code that previously read
 * {@code Display.getDisplayMode().getHeight()} (cursor Y flip, mouse angle)
 * can query {@link #getHeight()} instead.
 */
public final class Window {

    private static long handle;
    private static int width;
    private static int height;
    private static boolean fullscreen;
    private static boolean shouldClose;

    private static GLFWWindowSizeCallback sizeCallback;

    private Window() {
    }

    public static void init(int w, int h, boolean fullscreen) {
        Window.width = w;
        Window.height = h;
        Window.fullscreen = fullscreen;

        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        // OpenGL 3.3 core, forward-compatible. (Shader/VAO rewrite in 1.4.)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        long monitor = fullscreen ? glfwGetPrimaryMonitor() : 0L;
        handle = glfwCreateWindow(w, h, Game.title, monitor, 0L);
        if (handle == 0L) {
            throw new IllegalStateException("Failed to create GLFW window");
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer px = stack.mallocInt(1);
            IntBuffer py = stack.mallocInt(1);
            glfwGetFramebufferSize(handle, px, py);
            width = px.get(0);
            height = py.get(0);
        }

        if (!fullscreen) {
            GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (mode != null) {
                glfwSetWindowPos(handle,
                        (mode.width() - w) / 2,
                        (mode.height() - h) / 2);
            }
        }

        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1); // VSync on (replaces Display.setVSyncEnabled(true))
        glfwShowWindow(handle);

        // Keep cached width/height in sync if the framebuffer ever changes.
        sizeCallback = GLFWWindowSizeCallback.create((win, cw, ch) -> {
            width = cw;
            height = ch;
            glViewport(0, 0, cw, ch);
        });
        glfwSetWindowSizeCallback(handle, sizeCallback);

        // Bind LWJGL 3's GL function loader for the current context.
        GL.createCapabilities();
    }

    public static long handle() {
        return handle;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static boolean isFullscreen() {
        return fullscreen;
    }

    public static boolean isCloseRequested() {
        return glfwWindowShouldClose(handle);
    }

    public static void setTitle(String title) {
        glfwSetWindowTitle(handle, title);
    }

    public static void swapBuffers() {
        glfwSwapBuffers(handle);
    }

    public static void pollEvents() {
        glfwPollEvents();
        shouldClose = glfwWindowShouldClose(handle);
    }

    public static boolean shouldClose() {
        return shouldClose;
    }

    public static void setShouldClose(boolean value) {
        glfwSetWindowShouldClose(handle, value);
        shouldClose = value;
    }

    public static void hideCursor() {
        glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    }

    public static void destroy() {
        if (sizeCallback != null) {
            sizeCallback.free();
            sizeCallback = null;
        }
        if (handle != 0L) {
            glfwDestroyWindow(handle);
            handle = 0L;
        }
        glfwTerminate();
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if (callback != null) {
            callback.free();
        }
    }
}
package com.honor.blitzremake.input;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import com.honor.blitzremake.graphics.Window;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Input façade preserving the 2015 build's static boolean API
 * ({@code Input.up/down/left/right}) so game logic above stays unchanged.
 *
 * Replaces the LWJGL 2 polling APIs {@code org.lwjgl.input.Keyboard} and
 * {@code org.lwjgl.input.Mouse} with GLFW event callbacks. Callbacks are
 * registered against the {@link Window} handle once; {@link #pollInput()}
 * snapshots the keyboard state into the directional booleans each tick.
 *
 * A small {@link Mouse} shim exposes {@code getX/getY/isButtonDown} so the
 * cursor angle / shooting code in {@code Player} and {@code MyCursor} keeps
 * working with minimal edits. GLFW reports cursor Y top-down (0 at top),
 * which matches the flipped Y the old code computed via
 * {@code Display.getDisplayMode().getHeight() - Mouse.getY()}; the shim
 * therefore returns Y as-is and callers no longer flip.
 */
public final class Input {

    public static boolean up, down, left, right;

    private static GLFWKeyCallback keyCallback;
    private static GLFWMouseButtonCallback mouseButtonCallback;
    private static GLFWCursorPosCallback cursorPosCallback;

    private static final boolean[] keys = new boolean[GLFW_KEY_LAST + 1];
    private static double mouseX;
    private static double mouseY;
    private static boolean mouseButtonLeft;

    private Input() {
    }

    public static void create() {
        long window = Window.handle();

        keyCallback = GLFWKeyCallback.create((win, key, scancode, action, mods) -> {
            if (key < 0 || key >= keys.length) return;
            keys[key] = action != GLFW_RELEASE;
        });
        glfwSetKeyCallback(window, keyCallback);

        mouseButtonCallback = GLFWMouseButtonCallback.create((win, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                mouseButtonLeft = action != GLFW_RELEASE;
            }
        });
        glfwSetMouseButtonCallback(window, mouseButtonCallback);

        cursorPosCallback = GLFWCursorPosCallback.create((win, xpos, ypos) -> {
            mouseX = xpos;
            mouseY = ypos;
        });
        glfwSetCursorPosCallback(window, cursorPosCallback);
    }

    public static void pollInput() {
        up = keys[GLFW_KEY_W] || keys[GLFW_KEY_UP];
        down = keys[GLFW_KEY_S] || keys[GLFW_KEY_DOWN];
        left = keys[GLFW_KEY_A] || keys[GLFW_KEY_LEFT];
        right = keys[GLFW_KEY_D] || keys[GLFW_KEY_RIGHT];
    }

    public static boolean isKeyDown(int key) {
        return key >= 0 && key < keys.length && keys[key];
    }

    public static void dispose() {
        if (keyCallback != null) { keyCallback.free(); keyCallback = null; }
        if (mouseButtonCallback != null) { mouseButtonCallback.free(); mouseButtonCallback = null; }
        if (cursorPosCallback != null) { cursorPosCallback.free(); cursorPosCallback = null; }
    }

    /**
     * Mouse shim replacing the LWJGL 2 {@code org.lwjgl.input.Mouse} API.
     * GLFW reports Y top-down (0 at top, growing downward) so callers that
     * previously did {@code Display.getDisplayMode().getHeight() - Mouse.getY()}
     * should now use {@link #getY()} directly.
     */
    public static final class Mouse {
        private Mouse() {}

        public static int getX() { return (int) mouseX; }

        public static int getY() { return (int) mouseY; }

        public static boolean isButtonDown(int button) {
            return button == 0 && mouseButtonLeft;
        }
    }
}
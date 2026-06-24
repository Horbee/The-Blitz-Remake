package com.honor.launcher;

import java.nio.IntBuffer;
import java.util.LinkedHashSet;
import java.util.Set;

import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import com.honor.blitzremake.Game;
import com.honor.blitzremake.font.Font;
import com.honor.blitzremake.graphics.Shader;
import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;
import com.honor.blitzremake.util.Config;
import com.honor.blitzremake.util.Util;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * GLFW-based launcher replacing the 2015 Swing JFrame.
 *
 * <p>Owns its own GLFW window + OpenGL 3.3 core context (separate from the
 * game {@link com.honor.blitzremake.graphics.Window}). Renders a minimal
 * immediate-mode UI with the existing 3.3 shaders + {@link Font}: a
 * "WINDOWED" toggle, a scrollable list of available resolutions, and two
 * clickable buttons (SAVE LAUNCH / EXIT). The launcher event loop runs on
 * the calling (process main) thread and returns the chosen {@link Config}
 * when the user clicks SAVE LAUNCH, or {@code null} when the user clicks
 * EXIT / closes the window / presses ESC. The caller ({@link Game#main})
 * destroys the launcher window before creating the game window so the two
 * GL contexts never coexist.
 *
 * <p>The font atlas only contains uppercase letters + digits + ":!? ", so
 * all UI strings are rendered in uppercase.
 */
public class Launcher {

    private static final int WIN_W = 360;
    private static final int WIN_H = 460;

    private long window;

    private GLFWKeyCallback keyCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;
    private GLFWCursorPosCallback cursorPosCallback;

    private double mouseX, mouseY;
    private boolean mouseLeftDown;
    private boolean mouseLeftClicked;

    private boolean windowed = true;
    private Resolution[] resolutions;
    private int selectedResolution = 0;

    private boolean shouldClose;

    public Launcher() {
        Config saved = Util.loadConfig();
        windowed = saved.windowed();
        resolutions = enumerateResolutions();
        selectedResolution = pickMatching(resolutions, saved.width(), saved.height());
    }

    /**
     * Run the launcher event loop on the calling thread. Returns the chosen
     * config (SAVE LAUNCH), or null (EXIT / close / ESC).
     */
    public Config run() {
        createWindow();
        try {
            loop();
        } finally {
            destroyWindow();
        }
        if (shouldClose) {
            return null;
        }
        Resolution r = resolutions[selectedResolution];
        return new Config(windowed, r.width(), r.height());
    }

    public void destroy() {
        // Window + callbacks already freed in destroyWindow(); nothing else
        // to release. GLFW itself is terminated at the process boundary.
    }

    // ---------------------------------------------------------------------
    // Window / GL setup
    // ---------------------------------------------------------------------

    private void createWindow() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(WIN_W, WIN_H, "Blitz Launcher", 0L, 0L);
        if (window == 0L) {
            throw new IllegalStateException("Failed to create launcher window");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer px = stack.mallocInt(1);
            IntBuffer py = stack.mallocInt(1);
            glfwGetFramebufferSize(window, px, py);
        }

        GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (mode != null) {
            glfwSetWindowPos(window, (mode.width() - WIN_W) / 2, (mode.height() - WIN_H) / 2);
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();

        keyCallback = GLFWKeyCallback.create((win, key, sc, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                shouldClose = true;
            }
        });
        glfwSetKeyCallback(window, keyCallback);

        mouseButtonCallback = GLFWMouseButtonCallback.create((win, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                boolean down = action != GLFW_RELEASE;
                if (down && !mouseLeftDown) {
                    mouseLeftClicked = true;
                }
                mouseLeftDown = down;
            }
        });
        glfwSetMouseButtonCallback(window, mouseButtonCallback);

        cursorPosCallback = GLFWCursorPosCallback.create((win, x, y) -> {
            mouseX = x;
            mouseY = y;
        });
        glfwSetCursorPosCallback(window, cursorPosCallback);

        initGL();
    }

    private void initGL() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0.10f, 0.11f, 0.14f, 1.0f);
        glActiveTexture(GL_TEXTURE0);
        glViewport(0, 0, WIN_W, WIN_H);
    }

    private void destroyWindow() {
        if (keyCallback != null) { keyCallback.free(); keyCallback = null; }
        if (mouseButtonCallback != null) { mouseButtonCallback.free(); mouseButtonCallback = null; }
        if (cursorPosCallback != null) { cursorPosCallback.free(); cursorPosCallback = null; }
        if (window != 0L) {
            glfwDestroyWindow(window);
            window = 0L;
        }
    }

    // ---------------------------------------------------------------------
    // Event loop + immediate-mode UI
    // ---------------------------------------------------------------------

    private void loop() {
        // Load only what the launcher UI needs: the shaders + the font atlas.
        // (The game reloads both after its own context is created.)
        Shader.loadAll();
        Font.load();

        Matrix4f pr = Matrix4f.orthographic(0, WIN_W, WIN_H, 0, -1, 1);
        Shader.MENU.enable();
        Shader.MENU.setUniformMatrix("pr_matrix", pr);
        Shader.MENU.setUniform1i("tex", 0);
        Shader.MENU.disable();
        Shader.FONT.enable();
        Shader.FONT.setUniformMatrix("pr_matrix", pr);
        Shader.FONT.setUniform1i("tex", 0);
        Shader.FONT.disable();

        Vector3f white = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f dim = new Vector3f(0.6f, 0.6f, 0.65f);
        Vector3f accent = new Vector3f(1.0f, 0.85f, 0.2f);

        while (!shouldClose && !glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT);

            int y = 40;
            Font.drawString("LAUNCHER", 120, y, 24, 2, accent);
            y += 60;

            // WINDOWED toggle row
            String windowedLabel = "WINDOWED: " + (windowed ? "[X]" : "[ ]");
            Font.drawString(windowedLabel, 30, y, 20, 2, white);
            if (mouseLeftClicked && hit(mouseX, mouseY, 30, y - 4, 180, 28)) {
                windowed = !windowed;
            }
            y += 40;

            Font.drawString("RESOLUTIONS:", 30, y, 18, 2, dim);
            y += 30;

            int rowH = 26;
            int listTop = y;
            int maxVisible = 8;
            int visibleCount = Math.min(maxVisible, resolutions.length);
            for (int i = 0; i < visibleCount; i++) {
                int rowY = listTop + i * rowH;
                boolean selected = (i == selectedResolution);
                Vector3f color = selected ? accent : white;
                Font.drawString(resolutions[i].toString(), 40, rowY, 18, 2, color);
                if (mouseLeftClicked && hit(mouseX, mouseY, 30, rowY - 4, WIN_W - 60, rowH)) {
                    selectedResolution = i;
                }
            }
            y = listTop + visibleCount * rowH + 20;

            // Buttons
            int btnY = WIN_H - 60;
            boolean launchHover = hit(mouseX, mouseY, 30, btnY, 150, 36);
            boolean exitHover = hit(mouseX, mouseY, 200, btnY, 120, 36);
            Font.drawString("SAVE LAUNCH", 40, btnY + 8, 18, 2, launchHover ? accent : white);
            Font.drawString("EXIT", 220, btnY + 8, 18, 2, exitHover ? accent : dim);

            if (mouseLeftClicked) {
                if (launchHover) {
                    shouldClose = false;
                    mouseLeftClicked = false;
                    glfwSwapBuffers(window);
                    glfwPollEvents();
                    return;
                } else if (exitHover) {
                    shouldClose = true;
                }
            }

            mouseLeftClicked = false;
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private static boolean hit(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    // ---------------------------------------------------------------------
    // Resolution enumeration
    // ---------------------------------------------------------------------

    public static record Resolution(int width, int height) {
        @Override
        public String toString() {
            return width + "X" + height;
        }
    }

    private static Resolution[] enumerateResolutions() {
        long monitor = glfwGetPrimaryMonitor();
        GLFWVidMode.Buffer buffer = glfwGetVideoModes(monitor);
        Set<Resolution> dedup = new LinkedHashSet<>();
        if (buffer != null) {
            while (buffer.hasRemaining()) {
                GLFWVidMode mode = buffer.get();
                dedup.add(new Resolution(mode.width(), mode.height()));
            }
        }
        if (dedup.isEmpty()) {
            dedup.add(new Resolution(Game.WIDTH, Game.HEIGHT));
        }
        return dedup.toArray(new Resolution[0]);
    }

    private static int pickMatching(Resolution[] modes, int w, int h) {
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].width() == w && modes[i].height() == h) {
                return i;
            }
        }
        return 0;
    }
}
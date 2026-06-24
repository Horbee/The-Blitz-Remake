# The-Blitz-Remake — Migration Plan & Handoff

A 2015 Java LWJGL 2 game being modernized to LWJGL 3 + OpenGL 3.3 core +
modern tooling. This file is the single source of truth for the migration;
a fresh agent should be able to continue from "Remaining work" without any
other context.

---

## 1. Project overview

- **What:** "The-Blitz-Remake" — a top-down WWII arcade shooter originally
  built in 2015 on Java + LWJGL 2 + Slick-Util + Swing launcher.
- **Goal:** Full migration to LWJGL 3.3.4 with a modern OpenGL 3.3 core
  profile, GLFW window/input, OpenAL via STB vorbis, Gradle build, Java 21
  LTS. Two phases:
  - **Step 1** — platform/tooling migration (build, GLFW, GL 3.3, launcher,
    STB resources, verification). This is the current phase.
  - **Step 2** — code quality (JOML, AABB collision, SpotBugs/Checkstyle/CI).
- **Target runtime:** Java 21 LTS, desktop (Windows/Linux/macOS), fat jar
  via `com.gradleup.shadow`.

---

## 2. Conventions & decisions

- **Java 21 LTS** (not 17). Foojay `toolchain` auto-provisions Temurin 21
  to `~/.gradle/jdks/` on first build — no manual JDK install needed.
- **Gradle 8.10.2**, Kotlin DSL (`build.gradle.kts` +
  `settings.gradle.kts`), wrapper committed.
- **LWJGL 3.3.4** modules: `core`, `glfw`, `opengl`, `openal`, `stb`, with
  per-OS natives selected at config time via
  `org.gradle.internal.os.OperatingSystem`.
- **JOML 1.10.5** on the classpath now; adopted in Step 2.1 (drop
  hand-written `Matrix4f`/`Vector3f`).
- **`com.gradleup.shadow`** plugin for the fat jar. `tasks.shadowJar { }`
  accessor syntax (not FQN).
- **Small commits per sub-step** on the `dev` branch. Preserve per-file
  git history via `git mv` when restructuring.
- **Git author:** `Horbee <n.horox@gmail.com>` — already rewritten across
  all 8 commits via `git filter-branch`; local repo config set to match.
  All future commits must use this identity.
- **Step 2 skips tests**; lint only (SpotBugs + Checkstyle + CI).
- `*.jar` is **no longer gitignored** (deps come from Maven Central; the
  Gradle wrapper jar is committed by convention).
- **GLFW cursor Y is top-down** (0 at top). Old LWJGL2 `Mouse.getY()` was
  bottom-up, so the `Display.getHeight() - Mouse.getY()` flip that lived
  in `MyCursor` and `Player` was **removed in 1.3 — do not re-add it.**

---

## 3. Verification gate

After every sub-step:

```bash
./gradlew clean build     # compileJava + shadowJar + distZip
./gradlew compileJava     # quick check during a sub-step
```

JDK 21 is auto-provisioned by foojay on first invocation; no `JAVA_HOME`
setup required. If the build can't find a JDK, run
`./gradlew help --info` and look for the foojay provisioning log.

For runtime parity (Step 1.7): window opens, menu renders, a level loads,
keyboard + mouse input work, audio plays (after 1.6), ESC exits cleanly,
and `glGetError()` returns 0 (poll it or enable `KHR_debug`).

---

## 4. Progress checklist (Step 1)

- [x] **Prep `3a14f69`** — `.gitattributes` line-ending normalization
      (`eol=lf`, binary pins for `png/ogg/dll/pdn`).
- [x] **1.1 `5ea7435`** — Flatten `BlitzFinished/` → Gradle layout via
      `git mv` (155 renames, history preserved; deleted Eclipse meta +
      8 stale Windows DLLs; rewrote `.gitignore`).
- [x] **1.2 `441753b`** — Gradle build config (Kotlin DSL, LWJGL 3.3.4
      {core,glfw,opengl,openal,stb} + per-OS natives, JOML 1.10.5,
      `com.gradleup.shadow`, foojay JDK 21 toolchain). Verified
      `./gradlew help` + `dependencies` resolve clean.
- [x] **1.3 `0a78a35`** — Platform layer ported to GLFW + LWJGL 3 OpenAL.
      New: `graphics/Window.java`, `sound/Audio.java`,
      `sound/NoOpAudio.java`. Rewrote `input/Input.java`
      (GLFW-callback-fed static facade + `Input.Mouse` shim),
      `util/MyDisplay.java`, `Game.java` (ALC `long` handles),
      `util/Util.java` (`Window.hideCursor`), `graphics/Shader.java`
      (`glUniformMatrix4fv`), `launcher/Launcher.java`
      (`glfwGetVideoModes` + `Resolution` record), `font/Font.java`
      (CP1252 Hungarian comment → ASCII). Updated `MyCursor`,
      `Player`, `Menu`, `Ending`, `Level`. `./gradlew clean build`
      passes. Game compiles and runs (silent — audio stubbed; GL 2.1
      draw path still in place until 1.4).

---

## 5. Remaining work — Step 1

### 1.4 — OpenGL 2.1 → 3.3 core  *(biggest source commit)*

This is the next task. The codebase compiles against LWJGL 3 but still
issues GL 2.1 fixed-function / compatibility calls and uses `#version 120`
shaders. A 3.3 core context will reject most of these at runtime, so this
step is required before the game actually renders correctly.

Tasks:
- **Shaders** — rewrite all ~22 shader files under
  `src/main/resources/shaders/`:
  - `#version 120` → `#version 330 core`
  - `attribute` → `in` (vertex shader)
  - `varying` → `out` (vertex) / `in` (fragment)
  - `gl_FragColor` → declare `out vec4 fragColor;` and write to it
  - `texture2D(u, v)` → `texture(u, v)`
  - Drop any `gl_FragColor`-as-built-in usage.
- **`VertexArray`** — activate VAOs: `glGenVertexArrays` +
  `glBindVertexArray` around attribute pointer setup so attrib state
  lives in the VAO instead of being re-specified each draw.
- **`ShaderUtils`** — after `glLinkProgram`, validate
  `glGetProgramiv(ID, GL_LINK_STATUS)` and log the info log on failure.
  Cache attribute + uniform locations (query once at setup, not per
  draw).
- **Deprecated fixed-function removal** — sweep for any remaining
  `glEnable(GL_TEXTURE_2D)`, `glBegin/glEnd`, `glVertex`/`glTexCoord`,
  `glMatrixMode`, `glLoadIdentity`, `glOrtho`/`gluOrtho2D`, etc. A 3.3
  core context has none of these. Replace with shader-based draws.
- Verify: `./gradlew clean build`; run the game — window opens, menu
  and level render with the new shaders, no `GL_INVALID_OPERATION`
  errors.

### 1.5 — GLFW launcher + JSON config

- Replace the Swing UI in `launcher/Launcher.java` with a GLFW window
  (or a minimal immediate-mode UI drawn with the new 3.3 shaders).
  Swing itself isn't blocking, but the launcher still touches GLFW only
  for mode enumeration; making it a proper GLFW window keeps the
  surface story uniform.
- Replace `Config.xml` with JSON.
  - **OPEN QUESTION (TBD — confirm with user):** add
    `com.google.code.gson:gson` as a dependency, or hand-roll a tiny
    JSON parser to keep the dependency list lean?
- Persist chosen resolution + fullscreen toggle.

### 1.6 — STB resource loading + real audio

- **`Texture.java`** — replace `java.awt.image.BufferedImage`/`ImageIO`
  PNG decode with `stbi_load_from_memory`. Replace the
  `org.lwjgl.BufferUtils.createByteBuffer` call at line ~139 with
  `MemoryStack`/`MemoryUtil` (the legacy `org.lwjgl.BufferUtils` still
  ships in LWJGL 3 and compiles, but it's deprecated).
- **`Sound.java` + a real `Audio` impl** — load OGG via
  `stb_vorbis_decode_memory` → OpenAL buffer → source playback
  (`alGenSources`/`alGenBuffers`/`alSourcePlay`). Replace the
  `NoOpAudio` stub assigned to all 15 static `Sound` fields.
- **Classpath resource loading** — `new FileInputStream("res/...")`
  (relative-path, breaks from a fat jar) →
  `Thread.currentThread().getContextClassLoader().getResourceAsStream(...)`
  everywhere resources are opened.
- **`Font.java`** —
  - **OPEN QUESTION (TBD — confirm with user):** keep the existing AWT
    glyph atlas (just fix the resource loading path) or switch to
    STB-truetype for a fully AWT-free stack?
- After this step the game should have sound and load cleanly from the
  fat jar.

### 1.7 — Verification gate

- `./gradlew clean build` is clean.
- Run the game end-to-end: launcher → menu → level → shoot enemies →
  death/ending → ESC exits cleanly. Audio plays. No GL errors.
- `./gradlew shadowJar` produces a fat jar that runs standalone with
  natives extracted (verify on a clean machine / fresh JDK 21).

---

## 6. Remaining work — Step 2 (code quality)

- **2.1 JOML migration** — delete the hand-written `Matrix4f`/
  `Vector3f` and replace all usages with `org.joml.Matrix4f` /
  `org.joml.Vector3f` (already on the classpath from 1.2).
- **2.2 AABB collision** — `Entity.java` uses `java.awt.Rectangle` for
  collision. Replace with a small `Aabb` record (min/max `Vector3f` or
  `Vector2f`) + an `intersects` method; drop the `java.awt` dependency.
- **2.3 Lint + CI** — add SpotBugs + Checkstyle configs; add a GitHub
  Actions workflow that runs `./gradlew build` + lint on PRs. **Skip
  tests** (per user decision) — lint is the gate.

---

## 7. Known landmines / critical context

- `Font.java:14` had a CP1252-encoded Hungarian comment causing
  "unmappable character for encoding UTF-8". **Fixed in 1.3** — but if
  compile fails on a non-UTF-8 byte elsewhere, scan with:
  `python3 -c "import io; ..."` over `src/main/java` for bytes > 127.
- `Texture.java:139` uses `org.lwjgl.BufferUtils.createByteBuffer`
  (LWJGL 3 still ships `org.lwjgl.BufferUtils` as legacy compat, so it
  compiles). Replace with `MemoryStack`/`MemoryUtil` in 1.6.
- `Texture.java` uses `java.awt.image.BufferedImage`/`ImageIO` for PNG
  decode — replaced by STB in 1.6.
- Audio is a **no-op stub** in 1.3; the game runs silent until 1.6.
  `Sound.java` assigns `NoOpAudio` instances to all 15 static fields.
- Shaders still `#version 120`, draw calls still GL 2.1 — the game will
  not render correctly on a 3.3 core context until **1.4** is done.
- The `dev` branch history was rewritten (author rewrite to Horbee).
  If `dev` was previously pushed to a remote, the next push needs
  `--force-with-lease`.
- Git identity is set **locally** for this repo:
  `Horbee <n.horox@gmail.com>`. Do not change it.
- JOML is on the classpath but unused until Step 2.1.

---

## 8. Key file map

| File | Role |
|---|---|
| `build.gradle.kts` / `settings.gradle.kts` | Gradle config, LWJGL 3 + JOML deps, shadow plugin, foojay JDK 21 |
| `src/main/java/com/honor/blitzremake/Game.java` | Main loop, GLFW/AL lifecycle (`alDeviceHandle`/`alContextHandle` longs) |
| `src/main/java/com/honor/blitzremake/graphics/Window.java` | GLFW window holder (created in 1.3) |
| `src/main/java/com/honor/blitzremake/graphics/Shader.java` | GLSL program wrapper (uses `glUniformMatrix4fv`) |
| `src/main/java/com/honor/blitzremake/graphics/VertexArray.java` | Needs VAO activation in 1.4 |
| `src/main/java/com/honor/blitzremake/graphics/ShaderUtils.java` | Needs link validation + location caching in 1.4 |
| `src/main/java/com/honor/blitzremake/graphics/Texture.java` | Needs STB PNG decode in 1.6 |
| `src/main/java/com/honor/blitzremake/input/Input.java` | GLFW-callback-fed static facade + `Input.Mouse` shim |
| `src/main/java/com/honor/blitzremake/util/MyDisplay.java` | Thin wrapper over `Window.init` |
| `src/main/java/com/honor/blitzremake/util/Util.java` | `setBlankCursor` → `Window.hideCursor` |
| `src/main/java/com/honor/blitzremake/sound/Audio.java` | Audio interface |
| `src/main/java/com/honor/blitzremake/sound/NoOpAudio.java` | No-op stub (replaced in 1.6) |
| `src/main/java/com/honor/blitzremake/sound/Sound.java` | 15 static fields, currently all `NoOpAudio` |
| `src/main/java/com/honor/blitzremake/font/Font.java` | Glyph atlas; resource path fix in 1.6 |
| `src/main/java/com/honor/launcher/Launcher.java` | Swing UI + `glfwGetVideoModes`; full rewrite in 1.5 |
| `src/main/resources/shaders/` | ~22 `.vert`/`.frag` files to rewrite in 1.4 |

---

## 9. Current commit graph (dev branch)

```
0a78a35 Port platform layer to GLFW + LWJGL 3 OpenAL   ← HEAD of dev
441753b build: add Gradle (Kotlin DSL) + LWJGL 3 + JOML toolchain
5ea7435 Restructure: flatten BlitzFinished/ to repo root (Gradle layout)
3a14f69 chore: normalize line endings via .gitattributes
8e4b24b Update readme
43f0555 First Commit
f868e7d Update README.md
dc228ad Initial commit
```

All commits authored by `Horbee <n.horox@gmail.com>`.

---

## 10. Open questions for the user

1. **JSON config parser in 1.5** — add Gson as a dependency, or hand-roll
   a minimal parser to keep deps lean?
2. **Font in 1.6** — keep the existing AWT glyph atlas (just fix resource
   loading) or switch to STB-truetype for a fully AWT-free stack?

These are flagged inline in sections 5.1.5 and 5.1.6 as well. Ask the
user before starting those sub-steps.
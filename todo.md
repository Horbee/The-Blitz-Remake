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
- [x] **1.4** — OpenGL 2.1 → 3.3 core. `./gradlew clean build` passes.
      Shaders: rewrote all 22 `.vert`/`.frag` under
      `src/main/resources/shaders/` to `#version 330 core`
      (`attribute`→`in`, `varying`→`out`/`in`, `gl_FragColor`→
      `layout(location=0) out vec4 fragColor`, `texture2D`→`texture`,
      dropped illegal `uniform … = mat4(1.0)`/`= vec3(1.0)` initializers,
      added `layout(location=0) in vec3 position;` /
      `layout(location=1) in vec2 tc;` to every vertex shader that has
      those attributes). `level.fs` local var `texture` renamed to
      `texColor` (shadowed the `texture()` built-in, illegal in core).
      `VertexArray.java`: activated VAOs — `glGenVertexArrays`/
      `glBindVertexArray` around attrib setup (imported `GL30`), VAO
      records attrib + element-buffer state at construction;
      `bind`/`bindParticle`/`bindLightMesh` reduced to
      `glBindVertexArray(vao)` (dropped per-frame `getAttribLocation` +
      `glVertexAttribPointer` + `glEnableVertexAttribArray` queries —
      attribs now pinned to `Shader.VERTEX_ATTRIB=0`/
      `TEXTURE_ATTRIB=1` via the `layout` qualifiers). `unbind`→
      `glBindVertexArray(0)`. `ShaderUtils.java`: added
      `GL_LINK_STATUS` check after `glLinkProgram` with info-log on
      failure; compile + link failures now `throw RuntimeException`
      (was silent print-and-continue); removed load-time
      `glValidateProgram` (validated against wrong state, result
      unchecked). `LightRenderer.java:21`: `bind(Shader.LIGHT)`→
      `bindLightMesh(Shader.LIGHT)` — `light.vs` has no `tc`
      attribute, so plain `bind` queried `getAttribLocation("tc")`=-1
      and called `glEnableVertexAttribArray(-1)` (latent
      `GL_INVALID_OPERATION` on core). `Window.java`: added
      `glViewport(0,0,cw,ch)` to the framebuffer size callback.
      `build.gradle.kts`: macOS native classifier now picks
      `natives-macos-arm64` on Apple Silicon (was x64-only, blocked
      all runtime on M-series Macs); added `-XstartOnFirstThread` +
      `-Dorg.lwjgl.glfw.checkThread0=false` to `applicationDefaultJvmArgs`
      (macOS Cocoa main-thread rule). **Runtime verification on macOS
      is blocked by a pre-existing threading model bug, NOT by 1.4:**
      `Game.start()` spawns a worker thread (`Game.java:58
      new Thread(this)`) and all GLFW/GL calls happen there; Cocoa
      requires GLFW on the process main thread, so the JVM aborts
      (SIG 133) on Apple Silicon regardless of 1.4. The Launcher path
      (`Launcher.java:109 new Game().start()`) has the same problem.
      Step 1.7 verification must therefore run on Linux/Windows, OR
      1.5/1.7 must first restructure `Game` to run its loop on the
      calling thread (drop the `new Thread` wrapper). Shaders audited
      textually: zero `attribute`/`varying`/`gl_FragColor`/
      `texture2D`/uniform-initializer constructs remain; every `.vs`
      `out` matches its `.fs` `in` by name+type; only `gl_Position`
      built-in remains (legal in 330 core). No GLSL validator
      (`glslangValidator`/`glslc`) installed locally to compile-check
      offline; the `ShaderUtils` link-status check added here will
      surface any GLSL error loudly at startup on first real run.

---

## 5. Remaining work — Step 1

### 1.4 — OpenGL 2.1 → 3.3 core  *(DONE — see checklist §4)*

Completed. The full per-file changelog is in the progress checklist
above (§4, entry 1.4). Highlights: all 22 shaders rewritten to
`#version 330 core`; VAOs activated in `VertexArray.java`;
`ShaderUtils` now validates `GL_LINK_STATUS` and throws on compile/link
failure; `LightRenderer` switched to `bindLightMesh` (latent core-profile
`GL_INVALID_OPERATION` fix); `glViewport` added to the window size
callback; macOS arm64 native classifier + `-XstartOnFirstThread` added
to the Gradle run config.

**Caveat carried forward to 1.5 / 1.7:** the game's threading model
(`Game.start()` → `new Thread(this)`) puts all GLFW/GL calls on a worker
thread, which Cocoa rejects on macOS (SIG 133). This predates 1.4 and is
not fixed here. Either (a) verify 1.4's rendering on Linux/Windows, or
(b) fold a small threading fix into 1.5: drop the `new Thread` wrapper in
`Game` and run the loop on the calling (main) thread so GLFW is happy on
macOS. Option (b) is recommended since 1.5 already rewrites the launcher
and is the natural place to reconcile the lifecycle.

Tasks (reference — all done):
- **Shaders** — rewrote all 22 files under
  `src/main/resources/shaders/`:
  - `#version 120` → `#version 330 core`
  - `attribute` → `in` (vertex) / `layout(location=…)` qualifiers added
  - `varying` → `out` (vertex) / `in` (fragment)
  - `gl_FragColor` → `layout(location=0) out vec4 fragColor;`
  - `texture2D(u, v)` → `texture(u, v)`
  - Dropped illegal `uniform … = mat4(1.0)` / `= vec3(1.0)` initializers
- **`VertexArray`** — activated VAOs (`glGenVertexArrays` +
  `glBindVertexArray`); attrib state now lives in the VAO, per-draw
  `getAttribLocation`/`glVertexAttribPointer` calls removed.
- **`ShaderUtils`** — `glGetProgrami(GL_LINK_STATUS)` checked after
  `glLinkProgram`; failures throw with info log. Uniform locations were
  already cached in `Shader.java`; attribute locations are now fixed via
  `layout(location=…)` so no caching needed there.
- **Deprecated fixed-function removal** — audited: the codebase had
  **zero** immediate-mode / matrix-stack / `glEnable(GL_TEXTURE_2D)` /
  lighting / display-list / `glAlphaFunc` calls to begin with. Nothing
  to remove; the only core-profile blockers were the missing VAOs and
  the `#version 120` shaders, both fixed.
- Verify: `./gradlew clean build` passes. **Runtime render verification
  pending** (macOS blocked by the threading model above; run on
  Linux/Windows or after the 1.5 threading fix).

### 1.5 — GLFW launcher + JSON config

- Replace the Swing UI in `launcher/Launcher.java` with a GLFW window
  (or a minimal immediate-mode UI drawn with the new 3.3 shaders).
  Swing itself isn't blocking, but the launcher still touches GLFW only
  for mode enumeration; making it a proper GLFW window keeps the
  surface story uniform.
- **Threading fix (do this here, required for macOS):** drop the
  `new Thread(this)` wrapper in `Game.start()` (`Game.java:56-60`) and
  run the game loop on the calling thread. GLFW/GL must execute on the
  process main thread on macOS (Cocoa); the current worker-thread model
  aborts with SIG 133 on Apple Silicon and was the reason 1.4 could not
  be runtime-verified on macOS. Running on the main thread is also how
  GLFW apps are normally structured. Keep the fixed-timestep loop body
  unchanged.
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
  - **macOS prerequisite:** the 1.5 threading fix (loop on main thread)
    must be done first, otherwise GLFW aborts on Cocoa. On Linux/Windows
    the current worker-thread model happens to work, so 1.4's render
    changes can be verified there even before 1.5.
- `./gradlew shadowJar` produces a fat jar that runs standalone with
  natives extracted (verify on a clean machine / fresh JDK 21). Note:
  the fat jar currently does not extract/locate LWJGL natives when
  invoked with plain `java -jar` — the `run`/`installDist` tasks set up
  the native classpath via Gradle's `application` plugin. If standalone
  `java -jar` execution is a hard requirement, add LWJGL's
  `SharedLibraryLoader` extraction path or bundle natives beside the
  jar; otherwise document `./gradlew run` as the launch method.

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
- Shaders were `#version 120` with GL 2.1 draw calls — **fixed in 1.4**.
  All 22 shaders are now `#version 330 core`; VAOs are active;
  `ShaderUtils` validates link status. The game should render correctly
  on a 3.3 core context once the macOS threading prerequisite (below) is
  resolved or verification is done on Linux/Windows.
- **macOS threading landmine (new, surfaced during 1.4 verification):**
  `Game.start()` (`Game.java:56-60`) wraps the loop in
  `new Thread(this, "BlitzRemake").start()`, so all GLFW/GL calls run on
  a worker thread. Cocoa requires GLFW on the process main thread, so on
  Apple Silicon the JVM aborts with SIG 133 (`-XstartOnFirstThread` +
  `-Dorg.lwjgl.glfw.checkThread0=false` do not help — Cocoa itself
  rejects it). Linux/Windows tolerate off-main GLFW. Fix is scheduled
  for 1.5: run the loop on the calling thread. Until then, runtime
  verification of rendering must happen on Linux/Windows.
- `build.gradle.kts` native classifier now handles Apple Silicon
  (`natives-macos-arm64` vs `natives-macos`) — fixed in 1.4. The 1.2
  config only selected `natives-macos` (x64), which meant no LWJGL
  native could load at all on M-series Macs.
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
| `src/main/java/com/honor/blitzremake/graphics/VertexArray.java` | VAO-backed mesh (VAOs activated in 1.4); `bind`=`glBindVertexArray` |
| `src/main/java/com/honor/blitzremake/graphics/ShaderUtils.java` | GLSL program loader (link-status check + throw-on-failure added in 1.4) |
| `src/main/java/com/honor/blitzremake/graphics/Texture.java` | Needs STB PNG decode in 1.6 |
| `src/main/java/com/honor/blitzremake/input/Input.java` | GLFW-callback-fed static facade + `Input.Mouse` shim |
| `src/main/java/com/honor/blitzremake/util/MyDisplay.java` | Thin wrapper over `Window.init` |
| `src/main/java/com/honor/blitzremake/util/Util.java` | `setBlankCursor` → `Window.hideCursor` |
| `src/main/java/com/honor/blitzremake/sound/Audio.java` | Audio interface |
| `src/main/java/com/honor/blitzremake/sound/NoOpAudio.java` | No-op stub (replaced in 1.6) |
| `src/main/java/com/honor/blitzremake/sound/Sound.java` | 15 static fields, currently all `NoOpAudio` |
| `src/main/java/com/honor/blitzremake/font/Font.java` | Glyph atlas; resource path fix in 1.6 |
| `src/main/java/com/honor/launcher/Launcher.java` | Swing UI + `glfwGetVideoModes`; full rewrite in 1.5 |
| `src/main/resources/shaders/` | 22 `.vs`/`.fs` files, rewritten to `#version 330 core` in 1.4 |

---

## 9. Current commit graph (dev branch)

```
(1.4 uncommitted — working tree)   ← OpenGL 2.1 → 3.3 core
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
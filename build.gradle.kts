import org.gradle.internal.os.OperatingSystem

plugins {
    java
    application
    id("com.gradleup.shadow") version "8.3.5"
}

group = "com.honor"
version = "1.0.0"

// ---------------------------------------------------------------------------
// Java toolchain: target Java 21 (current LTS). The foojay plugin registered
// in settings.gradle.kts lets Gradle auto-download a matching JDK when one is
// not already installed, so the build is reproducible on any machine without
// manual JDK installation.
// ---------------------------------------------------------------------------
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Game.main is the process entry: it inits GLFW, runs the GLFW launcher
    // to collect a Config, then runs the game loop on the same (main) thread.
    mainClass = "com.honor.blitzremake.Game"
    // Allow LWJGL to use jemalloc for native memory allocation if present.
    // -XstartOnFirstThread is required by GLFW on macOS (Cocoa main-thread
    // rule); harmless on Linux/Windows. As of Step 1.5 the game loop runs on
    // the main thread (the Thread wrapper was removed), so the GLFW thread
    // check workaround is no longer needed.
    applicationDefaultJvmArgs = listOf(
        "-Dorg.lwjgl.system.allocator=jemalloc",
        "-XstartOnFirstThread"
    )
}

repositories {
    mavenCentral()
}

// ---------------------------------------------------------------------------
// LWJGL 3 dependency matrix.
//
// LWJGL ships per-OS native classifiers. We detect the build host OS and
// apply only the matching -natives classifier so a single build doesn't drag
// in three platforms' binaries. CI matrices build per-OS the same way.
//
// Modules required by the port (audited from the original LWJGL 2 imports):
//   lwjgl          core (BufferUtils/MemoryUtil/SharedLibrary)
//   lwjgl-glfw     window + input (replaces Display/Keyboard/Mouse/Cursor)
//   lwjgl-opengl   GL 3.3 core rendering (replaces org.lwjgl.opengl.*)
//   lwjgl-openal   audio (replaces org.lwjgl.openal.AL)
//   lwjgl-stb      PNG decode + OGG/vorbis (replaces Slick-Util + ImageIO)
// ---------------------------------------------------------------------------
val lwjglVersion = "3.3.4"
val jomlVersion = "1.10.5"

val os: String = when {
    OperatingSystem.current().isWindows -> "windows"
    OperatingSystem.current().isLinux -> "linux"
    OperatingSystem.current().isMacOsX -> "macos"
    else -> throw GradleException("Unsupported OS for LWJGL natives: ${OperatingSystem.current().name}")
}

// LWJGL 3.3.4 ships separate native jars for macOS x64 ("natives-macos") and
// Apple Silicon ("natives-macos-arm64"). Pick the one matching the JVM arch
// so the build runs on both Intel and M-series Macs.
val nativeClassifier = if (os == "macos" && System.getProperty("os.arch").startsWith("aarch64")) {
    "natives-macos-arm64"
} else {
    "natives-$os"
}

fun lwjgl(module: String) =
    "org.lwjgl:$module:$lwjglVersion"

fun lwjglNatives(module: String) =
    "org.lwjgl:$module:$lwjglVersion:$nativeClassifier"

dependencies {
    // LWJGL core modules (compile)
    implementation(lwjgl("lwjgl"))
    implementation(lwjgl("lwjgl-glfw"))
    implementation(lwjgl("lwjgl-opengl"))
    implementation(lwjgl("lwjgl-openal"))
    implementation(lwjgl("lwjgl-stb"))

    // Per-platform natives (runtime only)
    runtimeOnly(lwjglNatives("lwjgl"))
    runtimeOnly(lwjglNatives("lwjgl-glfw"))
    runtimeOnly(lwjglNatives("lwjgl-opengl"))
    runtimeOnly(lwjglNatives("lwjgl-openal"))
    runtimeOnly(lwjglNatives("lwjgl-stb"))

    // Math library for Step 2 (JOML). Available on the classpath from Step 1
    // so the GL 3.3 rewrite in 1.4 can use it directly where the hand-rolled
    // Matrix4f would otherwise need an interim bridge.
    implementation("org.joml:joml:$jomlVersion")

    // JSON config parser (Step 1.5). Gson is small (~280 KB) and keeps the
    // config read/write trivial versus a hand-rolled parser.
    implementation("com.google.code.gson:gson:2.11.0")
}

// ---------------------------------------------------------------------------
// Run task: keep CWD = project root so resources load from src/main/resources
// during development (Gradle puts the resources dir on the classpath
// automatically; this is just for any relative-path fallbacks still present
// before the 1.6 classpath-loading refactor).
// ---------------------------------------------------------------------------
tasks.named<JavaExec>("run") {
    workingDir = projectDir
}

// ---------------------------------------------------------------------------
// Compile flags: fail on unchecked warnings so the port surfaces anything
// the old Eclipse build silently tolerated.
// ---------------------------------------------------------------------------
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
}

tasks.withType<Jar> {
    manifest {
        attributes(
            "Implementation-Title" to "Blitz Remake",
            "Implementation-Version" to project.version
        )
    }
}

// The shadow plugin adds the 'shadowJar' task and a matching typed accessor
// 'tasks.shadowJar'. Use it so shadow-specific methods resolve in the
// Kotlin DSL without referencing the plugin's internal package by FQN.
tasks.shadowJar {
    archiveClassifier = "standalone"
    // Merge service files so LWJGL's ServiceLoader picks up the right
    // platform backend even inside the fat jar.
    mergeServiceFiles()
}
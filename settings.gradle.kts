pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // Auto-provision a JDK matching the toolchain spec in build.gradle.kts
    // when one isn't already installed locally. Lets the project build on a
    // clean clone with no manual JDK install.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "blitz-remake"
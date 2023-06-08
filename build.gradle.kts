plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

group = "net.okocraft"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.0.1")

    implementation("net.kyori:adventure-api:4.14.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.0")

    implementation("com.github.siroshun09.configapi:configapi-yaml:4.6.4")
    implementation("com.github.siroshun09.translationloader:translationloader:2.0.2")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    compileJava {
        options.release.set(17)
    }
    processResources {
        filesMatching(listOf("plugin.yml", "languages/en.yml", "languages/ja_JP.yml")) {
            expand("projectVersion" to version)
        }
    }
    shadowJar {
        minimize()
        val libPkg =  "net.okocraft.nonetherportaltrap.lib"

        relocate("com.github.siroshun09", libPkg)
        relocate("net.kyori", libPkg)
    }
}

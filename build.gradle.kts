plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
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
    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")

    implementation("org.jetbrains:annotations:23.0.0")

    implementation("net.kyori:adventure-api:4.12.0")
    implementation("net.kyori:adventure-platform-bukkit:4.1.2")

    implementation("com.github.siroshun09.configapi:configapi-yaml:4.6.0")
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
        relocate("com.github.siroshun09", "${group}.${name.toLowerCase()}.lib")
    }
}



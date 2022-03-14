import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.6.10"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public")
}

dependencies {
    implementation(project(":api"))
    implementation(kotlin("scripting-jvm"))
    implementation(kotlin("scripting-jvm-host"))
    implementation(kotlin("scripting-dependencies-maven"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
}

tasks.withType<ShadowJar> {
    // minimize()

    dependencies {
        // remove transitive guava dependency to save some space
        exclude(dependency("com.google.guava:guava:.*"))
    }
}

java.disableAutoTargetJvm() // compile against JDK 8 and use the latest Paper API

bukkit {
    name = "Cellulose"
    version = project.version as String
    author = "zlataovce"

    main = "me.kcra.cellulose.CellulosePlugin"

    apiVersion = "1.13"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
}
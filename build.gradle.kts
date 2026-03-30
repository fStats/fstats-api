val archivesBaseName: String by project
val mavenGroup: String by project
val modVersion: String by project

val javaVersion = JavaVersion.VERSION_25

plugins {
    alias(libs.plugins.fabric.loom)
}

base {
    archivesName.set(archivesBaseName)
}

group = mavenGroup
version = modVersion

repositories {
    maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    withSourcesJar()
}

tasks {
    jar {
        from("LICENSE")
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to project.version))
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release = javaVersion.toString().toInt()
    }
}

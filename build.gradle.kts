plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.marfien.rewibw"
version = "1.0.0"

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://jitpack.io")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    dependencies {
        //compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
        compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")

        compileOnly("org.projectlombok:lombok:1.18.30")
        annotationProcessor("org.projectlombok:lombok:1.18.30")
    }

    javaToolchains {
        java {
            targetCompatibility = JavaVersion.VERSION_1_8
            sourceCompatibility = JavaVersion.VERSION_1_8
        }
    }
}
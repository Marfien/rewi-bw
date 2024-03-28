plugins {
    id("java")
    alias(libs.plugins.shadow)
}

group = "dev.marfien.rewibw"
version = "1.0.0"

val lombokVersion = "1.18.30"
val lombok = "org.projectlombok:lombok:$lombokVersion"

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://jitpack.io")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    dependencies {
        compileOnly(lombok)
        annotationProcessor(lombok)
    }

    javaToolchains {
        java {
            targetCompatibility = JavaVersion.VERSION_1_8
            sourceCompatibility = JavaVersion.VERSION_1_8
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    alias(libs.plugins.shadow)
}

group = "dev.marfien.rewibw"

version = (project.findProperty("projectVersion")?.toString() ?: "{version}").replace("{version}", version as String)

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

    tasks.processResources {
        // Replace all {version} tokens in resources
        filter<ReplaceTokens>(mapOf("tokens" to mapOf(
            "version" to version
        )))
    }
}
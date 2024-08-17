import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    alias(libs.plugins.shadow)
}

group = "dev.marfien.rewibw"
version = (project.findProperty("projectVersion")?.toString() ?: "{version}").replace("{version}", version as String).replace('/', '-')

val lombokVersion = "1.18.30"
val lombok = "org.projectlombok:lombok:$lombokVersion"

tasks {
    register<Exec>("installSpigot") {

        workingDir = project.layout.buildDirectory.dir("installSpigot").get().asFile
        commandLine("bash", "install.sh")
    }

    build {
        dependsOn("installSpigot")
    }
}

subprojects {
    apply(plugin = "java")

    group = rootProject.group
    version = rootProject.version

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
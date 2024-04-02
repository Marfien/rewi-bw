import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.shadow)
}

group = parent!!.group
version = parent!!.version

dependencies {
    // Obtainable via BuildTools
    compileOnly(libs.spigot)
    implementation(libs.effectlib)
    implementation(project(":shared"))
    implementation(project(":fakeentities"))
    compileOnly(libs.noteblockapi)
}

tasks.withType<ShadowJar> {
    relocate("org.yaml.snakeyaml", "shadow.org.yaml.snakeyaml")
}
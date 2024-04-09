import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    // Obtainable via BuildTools
    compileOnly(libs.spigot)
    compileOnly(libs.noteblockapi)

    implementation(libs.effectlib)
    implementation(project(":shared"))
    implementation(project(":fakeentities"))
}

tasks.withType<ShadowJar> {
    relocate("org.yaml.snakeyaml", "shadow.org.yaml.snakeyaml")
}
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
}
plugins {
    id("java-library")
}

group = parent!!.group
version = parent!!.version

dependencies {
    // Obtainable via BuildTools
    api(libs.spigot.api)
    api(libs.effectlib)
}
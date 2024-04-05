
plugins {
    id("java-library")
}

group = parent!!.group
version = parent!!.version

dependencies {
    // Obtainable via BuildTools
    compileOnly(libs.log4j.api)
    compileOnly(libs.spigot.api) {
        exclude(group = "net.md-5", module = "bungeecord-chat")
    }
    api(libs.effectlib)
    api(libs.configurate)
}
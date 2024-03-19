plugins {
    id("java-library")
}

group = parent!!.group
version = parent!!.version

dependencies {
    // Obtainable via BuildTools
    api(libs.spigot.api) {
        exclude(group = "net.md-5", module = "bungeecord-chat")
    }
    api(libs.effectlib)
}
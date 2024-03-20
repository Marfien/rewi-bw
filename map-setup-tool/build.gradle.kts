plugins {
    alias(libs.plugins.shadow)
}

group = parent!!.group
version = parent!!.version

dependencies {
    compileOnly(libs.spigot.api) {
        exclude(group = "net.md-5", module = "bungeecord-chat")
    }
    implementation(libs.effectlib)
    implementation(project(":shared"))
}
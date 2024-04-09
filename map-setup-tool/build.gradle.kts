plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.spigot.api) {
        exclude(group = "net.md-5", module = "bungeecord-chat")
    }
    implementation(libs.effectlib)
    implementation(project(":shared"))
    compileOnly(libs.log4j.api)
}
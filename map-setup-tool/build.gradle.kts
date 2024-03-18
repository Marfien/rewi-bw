group = parent!!.group
version = parent!!.version

dependencies {
    // Obtainable via BuildTools
    compileOnly(libs.spigot)
    implementation(libs.effectlib)
}
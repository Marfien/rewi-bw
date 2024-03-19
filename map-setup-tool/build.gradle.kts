group = parent!!.group
version = parent!!.version

dependencies {
    // Obtainable via BuildTools
    compileOnly(libs.spigot.api)
    implementation(libs.effectlib)
    implementation(project(":shared"))
}
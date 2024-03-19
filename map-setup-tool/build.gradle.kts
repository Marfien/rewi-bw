group = parent!!.group
version = parent!!.version

dependencies {
    compileOnly(libs.spigot.api)
    implementation(libs.effectlib)
    implementation(project(":shared"))
}
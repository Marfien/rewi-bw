plugins {
    id("java-library")
}

group = parent!!.group
version = parent!!.version

dependencies {
    compileOnly(libs.spigot)
}
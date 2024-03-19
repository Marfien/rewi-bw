plugins {
    id("java-library")
}

group = parent!!.group
version = parent!!.version

dependencies {
    api(libs.spigot)
}
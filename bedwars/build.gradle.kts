group = parent!!.group
version = parent!!.version

repositories {
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    // Obtainable via BuildTools
    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
    implementation("com.github.Slikey:EffectLib:5.10-SNAPSHOT")
}
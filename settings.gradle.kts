rootProject.name = "rewi-bw"

dependencyResolutionManagement {
    repositories {
        repositories {
            mavenCentral()
            mavenLocal()
            maven("https://jitpack.io")
            maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        }
    }
}

include("anti-reduce-agent")
include("bedwars")
include("map-setup-tool")
include("shared")
include("fakeentities")

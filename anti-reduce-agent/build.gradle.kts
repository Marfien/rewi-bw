group = parent!!.group
version = parent!!.version

dependencies {
    implementation("org.javassist:javassist:3.27.0-GA")
}

tasks.withType<Jar>() {
    manifest {
        attributes(
            "Can-Redefine-Classes"      to "true",
            "Can-Retransform-Classes"   to "true",
            "Premain-Class"             to "dev.marfien.rewibw.agent.AntiReduceAgent",
            "Agent-Class"               to "dev.marfien.rewibw.agent.AntiReduceAgent"
        )
    }
}
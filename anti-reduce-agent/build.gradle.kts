group = parent!!.group
version = parent!!.version

dependencies {
    implementation(libs.javassist)
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
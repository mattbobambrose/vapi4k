plugins {
    kotlin("jvm") version "2.0.20"
}

dependencies {
    implementation(project(":vapi4k-core"))
}

kotlin {
    jvmToolchain(11)
}

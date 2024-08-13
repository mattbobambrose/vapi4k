plugins {
    kotlin("jvm") version "2.0.10"
}

dependencies {
    implementation(project(":vapi4k-core"))
}

kotlin {
    jvmToolchain(11)
}

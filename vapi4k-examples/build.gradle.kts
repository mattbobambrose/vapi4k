plugins {
    kotlin("jvm") version "2.0.10"
}

repositories {
    google()
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.mattbobambrose.vapi4k:vapi4k-core:1.2.0")
}

kotlin {
    jvmToolchain(17)
}

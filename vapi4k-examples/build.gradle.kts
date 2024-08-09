plugins {
    kotlin("jvm") version "2.0.10"
}

dependencies {
    implementation(project(":vapi4k-core"))
    // implementation("com.github.mattbobambrose.vapi4k:vapi4k-core:1.3.2")
}

kotlin {
    jvmToolchain(17)
}

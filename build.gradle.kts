plugins {
    alias(libs.plugins.jvm) apply false
    alias(libs.plugins.versions) apply true
    alias(libs.plugins.kotlinter) apply false
}

val versionStr: String by extra
val kotlinLib = libs.plugins.jvm.get().toString().split(":").first()
val ktlinterLib = libs.plugins.kotlinter.get().toString().split(":").first()

allprojects {
    extra["versionStr"] = "1.3.14"
    extra["releaseDate"] = "09/13/2024"
    group = "com.github.mattbobambrose.vapi4k"
    version = versionStr

    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

subprojects {
    apply {
        plugin("java")
        plugin("maven-publish")
        plugin(kotlinLib)
        plugin(ktlinterLib)
    }
}

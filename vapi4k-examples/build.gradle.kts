plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlinter)
    id("maven-publish")
    `java-library`
}

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

val versionStr: String by extra
val groupStr: String by extra
val releaseDate: String by extra

description = project.name
version = versionStr

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = description
            version = versionStr
            from(components["java"])
        }
    }
}

dependencies {
    api(libs.kotlin.reflect)

    api(libs.ktor.client.core)
    api(libs.ktor.client.cio)
    api(libs.ktor.client.encoding)
    api(libs.ktor.client.content.negotiation)

    api(libs.ktor.server.core)
    api(libs.ktor.server.cio)
    api(libs.ktor.server.compression)
    api(libs.ktor.server.content.negotiation)
    api(libs.ktor.server.call.logging)
    api(libs.ktor.server.html.builder)
    api(libs.ktor.server.metrics.micrometer)

    api(libs.ktor.serialization)
    api(libs.micrometer.registry.prometheus)

    api(libs.hikari)
    api(libs.pgjdbc.ng)
    api(libs.postgres)
    api(libs.exposed.core)
    api(libs.exposed.jdbc)
    api(libs.exposed.json)
    api(libs.exposed.kotlin.datetime)

    api(libs.kotlin.logging)
    api(libs.logback.classic)

    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.ktor.server.tests)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain(17)

    sourceSets.all {
        languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
    }
}

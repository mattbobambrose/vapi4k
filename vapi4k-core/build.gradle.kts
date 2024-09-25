import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.dokka.DokkaConfiguration.Visibility
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.serialization)
    alias(libs.plugins.config)
    alias(libs.plugins.dokka)
    `java-library`
}

val versionStr: String by extra
val releaseDate: String by extra

description = project.name

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

buildConfig {
    useKotlinOutput()
    packageName(project.group.toString())
    documentation.set("Generated by BuildConfig plugin")
    buildConfigField("String", "APP_NAME", "\"${project.name}\"")
    buildConfigField("String", "VERSION", provider { "\"${project.version}\"" })
    buildConfigField("String", "RELEASE_DATE", "\"$releaseDate\"")
    buildConfigField("long", "BUILD_TIME", "${System.currentTimeMillis()}L")
}

dependencies {
    api(project(":vapi4k-utils"))

    api(libs.kotlin.reflect)

    api(libs.ktor.client.core)
    api(libs.ktor.client.cio)
    api(libs.ktor.client.websockets)
    api(libs.ktor.client.encoding)
    api(libs.ktor.client.content.negotiation)

    api(libs.ktor.server.core)
    api(libs.ktor.server.cio)
    api(libs.ktor.server.websockets)
    api(libs.ktor.server.compression)
    api(libs.ktor.server.content.negotiation)
    api(libs.ktor.server.call.logging)
    api(libs.ktor.server.html.builder)
    api(libs.ktor.server.metrics.micrometer)

    api(libs.ktor.serialization)
    api(libs.micrometer.registry.prometheus)

    api(libs.exposed.kotlin.datetime)

    api(libs.kotlin.logging)
    api(libs.logback.classic)

    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.ktor.server.tests)
    testImplementation(kotlin("test"))
}

sourceSets {
    named("main") {
        java.srcDir("build/generated/sources/buildConfig/main")
    }
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain(11)

    sourceSets.all {
        languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
    }
}

tasks.withType<Zip> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks {
    register<Jar>("uberJar") {
        archiveClassifier.set("uber")
        from(sourceSets.main.get().output)
        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
        })
//        manifest {
//            attributes["Main-Class"] = "com.vapi4k.ApplicationKt"
//        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    withType<DependencyUpdatesTask> {
        rejectVersionIf {
            listOf("BETA", "-RC").any { candidate.version.uppercase().contains(it) }
        }
    }
}

kotlinter {
    failBuildWhenCannotAutoFormat = false
    ignoreFailures = true
    reporters = arrayOf("checkstyle", "plain")
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets.configureEach {
        documentedVisibilities.set(
            setOf(
                Visibility.PUBLIC,
                // Visibility.PROTECTED,
            )
        )

        perPackageOption {
            matchingRegex.set(".*internal.*")
            suppress.set(true)
        }
    }
}

tasks.withType<DokkaTask>().configureEach {
//      "customAssets": ["${file("assets/my-image.png")}"],
//      "customStyleSheets": ["${file("assets/my-styles.css")}"],
//      "separateInheritedMembers": false,
//      "templatesDir": "${file("dokka/templates")}",
//      "mergeImplicitExpectActualDeclarations": false
    val dokkaBaseConfiguration = """
    {
      "footerMessage": "Vapi4k"
    }
    """
    pluginsMapConfiguration.set(
        mapOf(
            // fully qualified plugin name to json configuration
            "org.jetbrains.dokka.base.DokkaBase" to dokkaBaseConfiguration
        )
    )
}

tasks.dokkaHtml.configure {
//    outputDirectory.set(buildDir.resolve("dokka"))

    dokkaSourceSets {
        named("main") {
            //displayName.set("Vapi4k")
            noStdlibLink.set(true)
            noJdkLink.set(true)

            // Exclude everything first
            perPackageOption {
                matchingRegex.set("com.vapi4k.*") // will match kotlin and all sub-packages of it
                suppress.set(true)
            }

            // Include specific packages
            perPackageOption {
                matchingRegex.set("com.vapi4k.api.*") // will match kotlin and all sub-packages of it
                includeNonPublic.set(false)
                reportUndocumented.set(false)
                skipDeprecated.set(false)
                suppress.set(false)
            }
        }
    }
}

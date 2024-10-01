import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.dokka.DokkaConfiguration.Visibility
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.serialization)
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

dependencies {
    implementation(libs.kotlin.serialization)
    implementation(libs.ktor.server.core)

    implementation(libs.kotlin.logging)
    implementation(libs.logback.classic)

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
                matchingRegex.set("com.vapi4k.utils.api.*") // will match kotlin and all sub-packages of it
                includeNonPublic.set(false)
                reportUndocumented.set(false)
                skipDeprecated.set(false)
                suppress.set(false)
            }
        }
    }
}

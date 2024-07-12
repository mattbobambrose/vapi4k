import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

val css_version: String by project
val exposed_version: String by project
val h2_version: String by project
val kotlin_version: String by project
val kotlinx_html_version: String by project
val logback_version: String by project
val logging_version: String by project
val postgres_version: String by project
val ktor_version: String by project
val hikariVersion: String by project
val pgjdbcVersion: String by project
val postgresVersion: String by project
val exposedVersion: String by project

val mainClassName = "com.vapi4k.ApplicationKt"

plugins {
    val kotlinVersion: String by System.getProperties()
    val ktorVersion: String by System.getProperties()
    val versionsVersion: String by System.getProperties()
    id("maven-publish")
    kotlin("jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
    id("io.ktor.plugin") version ktorVersion
    id("com.github.ben-manes.versions") version versionsVersion
    //id("io.gitlab.arturbosch.detekt") version "1.23.6"
}

val vstr = "1.0.1"

group = "com.vapi4k"
version = vstr

application {
    mainClass.set("com.vapi4k.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-compression-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-html-builder-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinx_html_version")
    implementation("org.jetbrains:kotlin-css-jvm:$css_version")
    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("com.h2database:h2:$h2_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("io.ktor:ktor-server-cio-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.github.oshai:kotlin-logging-jvm:$logging_version")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng-all:$pgjdbcVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.vapi4k"
            artifactId = "vapi4k-core"
            version = vstr
            from(components["java"])
        }
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.register<Jar>("uberJar") {
    archiveClassifier.set("uber")

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    manifest {
        attributes["Main-Class"] = "com.vapi4k.ApplicationKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        listOf("BETA").any { candidate.version.uppercase().contains(it) }
    }
}

//tasks.findByName("lintKotlinCommonMain")?.apply {
//    dependsOn("kspCommonMainKotlinMetadata")
//}


//detekt {
//    buildUponDefaultConfig = true // preconfigure defaults
//    allRules = false // activate all available (even unstable) rules.
//    config.setFrom("$projectDir/config/detekt/detekt.yml") // custom config defining rules to run
//    baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt
//}
//
//tasks.withType<Detekt>().configureEach {
//    reports {
//        html.required.set(true) // observe findings in your browser with structure and code snippets
//        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
//        // similar to the console output, contains issue signature to manually edit baseline files
//        txt.required.set(true)
//        // standardized SARIF format to support integrations with GitHub Code Scanning
//        sarif.required.set(true)
//        md.required.set(true) // simple Markdown format
//    }
//}

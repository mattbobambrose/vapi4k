/*
 * Copyright © 2024 Matthew Ambrose (mattbobambrose@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

plugins {
    alias(libs.plugins.jvm) apply false
    alias(libs.plugins.versions) apply false
    alias(libs.plugins.kotlinter) apply false
}

allprojects {
    extra["versionStr"] = "1.3.2"
    extra["releaseDate"] = "08/09/2024"
    group = "com.github.mattbobambrose.vapi4k"
}

val kotlinLib = libs.plugins.jvm.get().toString().split(":").first()
val ktlinterLib = libs.plugins.kotlinter.get().toString().split(":").first()

subprojects {
    apply {
        plugin("java")
        plugin("maven-publish")
        plugin(kotlinLib)
        plugin(ktlinterLib)
    }
}

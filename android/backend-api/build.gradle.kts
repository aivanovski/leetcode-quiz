import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinSerialization)
    id("java-library")
}

val appGroupId = "com.github.ai.split"

group = appGroupId
version = libs.versions.appVersion

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    testImplementation(libs.junit.engine)
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)

    implementation(libs.kotlinx.json)
}
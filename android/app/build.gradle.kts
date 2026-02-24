import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.room)
    alias(libs.plugins.ktlint)
}

val debugCredentials = readDebugCredentials()

val debugKeystoreFile = file("../keys/debug.keystore")
val publicKeystoreFile = file("public.keystore")

android {
    namespace = "com.aivanovski.leetcode.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aivanovski.leetcode.android"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "0.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = if (debugKeystoreFile.exists()) debugKeystoreFile else publicKeystoreFile
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")

            buildConfigField(
                "String[]",
                "DEBUG_EMAILS",
                debugCredentials.map { (user, _) -> user }
                    .joinToString(prefix = "{", postfix = "}") { "\"$it\"" }
            )

            buildConfigField(
                "String[]",
                "DEBUG_PASSWORDS",
                debugCredentials.map { (_, password) -> password }
                    .joinToString(prefix = "{", postfix = "}") { "\"$it\"" }
            )
        }

        release {
            isMinifyEnabled = false
            buildConfigField(
                "String[]",
                "DEBUG_EMAILS",
                "{}"
            )
            buildConfigField(
                "String[]",
                "DEBUG_PASSWORDS",
                "{}"
            )
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    kotlin {
        compilerOptions {
            jvmToolchain(21)
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.unit)
    testImplementation(libs.junit4)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.truth)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.vm)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.lifecycle.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.viewMaterial)
    implementation(libs.androidx.icons)
    implementation(libs.androidx.navigation3Runtime)
    implementation(libs.androidx.navigation3Ui)
    implementation(libs.androidx.constraintLayoutCompose)

    // Arrow
    implementation(libs.arrowCore)
    implementation(libs.arrowCoroutines)

    // Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // Timber
    implementation(libs.timber)

    // Koin
    implementation(libs.koin)
    implementation(libs.koin.android)

    // Network
    implementation(libs.ktor.clientCore)
    implementation(libs.ktor.clientOkhttp)
    implementation(libs.ktor.clientLogging)
    implementation(libs.ktor.clientNegotiation)
    implementation(libs.ktor.clientAuth)
    implementation(libs.ktor.serializationJson)

    // Preferences
    implementation(libs.ksprefs)

    // Api
    implementation(project(":backend-api"))
}

fun readDebugCredentials(): List<Pair<String, String>> {
    val propertiesFile = File(project.rootProject.rootDir, "debug.properties")
    if (!propertiesFile.exists()) {
        return emptyList()
    }

    val properties = Properties().apply {
        FileInputStream(propertiesFile).use {
            load(it)
        }
    }

    val users = properties.getProperty("debugUsers").trim().split(",")
    val passwords = properties.getProperty("debugPasswords").trim().split(",")
    val credentials = if (users.size == passwords.size) users.zip(passwords) else emptyList()

    project.logger.lifecycle("Debug credentials: $credentials")

    return credentials
}

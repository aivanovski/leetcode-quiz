plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.protobuf)
    id("java-library")
}

val appGroupId = "com.github.ai.leetcodequiz.api"

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

    api(libs.protobuf.kotlin)

    // Makes scalapb/scalapb.proto, imported by the shared schema, available to protoc.
    protobuf("com.thesamet.scalapb:scalapb-runtime_2.13:0.11.17")
}

sourceSets {
    main {
        proto {
            srcDir("../../backend/api/src/main/protobuf")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }

    generateProtoTasks {
        all().configureEach {
            builtins {
                create("kotlin")
            }
        }
    }
}

// Proto sources are compile-time inputs. Packaging them would duplicate the standard
// Google schemas already present in protobuf-java when Android merges Java resources.
tasks.processResources {
    exclude("**/*.proto")
}

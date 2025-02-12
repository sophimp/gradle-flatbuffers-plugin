import java.util.Properties

plugins {
    id("com.gradle.plugin-publish") version "1.3.1"
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
}

group = "io.sophimp"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

sourceSets {
    main {
        java {
            srcDirs("src/main/groovy")
        }
    }
}

gradlePlugin {
    website.set("https://github.com/sophimp/gradle-flatbuffers-plugin")
    vcsUrl.set("https://github.com/sophimp/gradle-flatbuffers-plugin")
    plugins {
        create("flatBuffersPlugin") {
            id = "io.sophimp.flatbuffers"
            implementationClass = "io.sophimp.flatbuffers.plugin.FlatBuffersPlugin"
            displayName = "gralde flatBuffer plugin"
            description = "wrap google flatBuffers in gradle plugin"
            tags.set(listOf("serialization", "build", "codegen"))
        }
    }
}


val localProperties = Properties()
file("local.properties").inputStream().use { fis ->
    localProperties.load(fis)
}

//license {
//    header file('codequality/HEADER')
//    strictCheck true
//    excludes([ "**/*.json", "**/*.html", "**/*.js" ])
//}

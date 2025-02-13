import java.util.Properties

plugins {
    id("com.gradle.plugin-publish") version "1.3.1"
    `kotlin-dsl`
}

// 读取 local.properties 文件
val localProperties = Properties().apply {
    val localPropertiesFile = File("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}
// 将 local.properties 中的配置设置为项目属性
localProperties.forEach { key, value ->
    if (key is String && value is String) {
        project.extensions.extraProperties.set(key, value)
    }
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

//license {
//    header file('codequality/HEADER')
//    strictCheck true
//    excludes([ "**/*.json", "**/*.html", "**/*.js" ])
//}

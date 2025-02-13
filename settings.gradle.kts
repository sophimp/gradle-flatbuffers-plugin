import java.util.Properties

// settings.gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
//    versionCatalogs {
//        create("libs") {
//            from(files("gradle/libs.versions.toml"))
//        }
//    }
}

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
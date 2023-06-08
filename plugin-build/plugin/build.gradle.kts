// Copyright © Matt Jones and Contributors. Licensed under the MIT Licence (MIT). See LICENCE.md in the repository root
// for more information.

plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(gradleApi())
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")

    testImplementation(TestingLib.JUNIT)
}

kotlin {
    jvmToolchain(17)
}

gradlePlugin {
    plugins {
        create(PluginCoordinates.ID) {
            id = PluginCoordinates.ID
            implementationClass = PluginCoordinates.IMPLEMENTATION_CLASS
            version = PluginCoordinates.VERSION
        }
    }
}

// Configuration Block for the Plugin Marker artifact on Plugin Central
//pluginBundle {
//    website = PluginBundle.WEBSITE
//    vcsUrl = PluginBundle.VCS
//    description = PluginBundle.DESCRIPTION
//    tags = PluginBundle.TAGS
//
//    plugins {
//        getByName(PluginCoordinates.ID) {
//            displayName = PluginBundle.DISPLAY_NAME
//        }
//    }
//
//    mavenCoordinates {
//        groupId = PluginCoordinates.GROUP
//        artifactId = PluginCoordinates.ID
//        version = PluginCoordinates.VERSION
//    }
//}

tasks.create("setupPluginUploadFromEnvironment") {
    doLast {
        val key = System.getenv("GRADLE_PUBLISH_KEY")
        val secret = System.getenv("GRADLE_PUBLISH_SECRET")

        if (key == null || secret == null) {
            throw GradleException("gradlePublishKey and/or gradlePublishSecret are not defined environment variables")
        }

        System.setProperty("gradle.publish.key", key)
        System.setProperty("gradle.publish.secret", secret)
    }
}
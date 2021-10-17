pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = ("novelrt-plugin")

include(":example")
includeBuild("plugin-build")

// Copyright © Matt Jones and Contributors. Licensed under the MIT Licence (MIT). See LICENCE.md in the repository root
// for more information.

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include("novelrt-plugin")
project(":novelrt-plugin").projectDir = file("plugin")

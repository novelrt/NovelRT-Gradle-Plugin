// Copyright © Matt Jones and Contributors. Licensed under the MIT Licence (MIT). See LICENCE.md in the repository root
// for more information.

plugins {
    id("dev.novelrt.novelrt-plugin")
    application
}

application {
    mainModule.set("dev.novelrt.examplemodule")
}

novelrt {
    sdk {
        binariesDir.set(project.projectDir.resolve("novelrt-binaries"))
        engineResourcesDir.set(project.projectDir.resolve("novelrt-resources"))
    }
}

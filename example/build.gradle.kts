// Copyright © Matt Jones and Contributors. Licensed under the MIT Licence (MIT). See LICENCE.md in the repository root
// for more information.

plugins {
    id("novelrt-plugin")
}

application {
    mainModule.set("dev.novelrt.examplemodule")
}

novelrt {
    binariesLocation.set(project.projectDir.resolve("novelrt-binaries"))
}

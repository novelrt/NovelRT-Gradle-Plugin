plugins {
    id("novelrt-plugin")
}

application {
    mainModule.set("dev.novelrt.examplemodule")
}

novelrt {
    binariesLocation.set(project.projectDir.resolve("novelrt-binaries"))
}

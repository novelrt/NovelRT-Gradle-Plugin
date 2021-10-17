package dev.novelrt.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.JavaCompile

abstract class ConfigureModuleBuildTask : DefaultTask() {
    private val novelRTExtension = project.extensions.getByType(NovelRTExtension::class.java)
    private val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)

    @get:Input
    val mainModule: String? get() = novelRTExtension.mainModule.orNull

    @get:Input
    val mainOutput: String get() = sourceSets.getByName("main").output.asPath

    @TaskAction
    fun configure() {
        if (mainModule != null) {
            val compileJavaTask = project.tasks.getByName("compileJava") as JavaCompile

            val patchModuleArg = "$mainModule=$mainOutput"

            compileJavaTask.options.compilerArgs.addAll(listOf(
                "--patch-module", patchModuleArg
            ))
        } else {
            project.logger.warn("Couldn't add a --patch-module argument because mainModule is not set " +
                    "in the application plugin or in novelrt. This can cause issues with building the project.")
        }
    }
}
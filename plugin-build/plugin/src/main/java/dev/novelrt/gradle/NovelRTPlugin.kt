// Copyright © Matt Jones and Contributors. Licensed under the MIT Licence (MIT). See LICENCE.md in the repository root
// for more information.

package dev.novelrt.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.compile.JavaCompile


abstract class NovelRTPlugin : Plugin<Project> {
    companion object {
        const val EXTENSION_NAME = "novelrt"
    }

    lateinit var project: Project
    override fun apply(project: Project) {
        this.project = project

        project.plugins.apply(JavaPlugin::class.java)

        project.extensions.create(EXTENSION_NAME, NovelRTExtension::class.java, project)

        configureCompileTask()
        configureRunTask()
        createCombineResourcesTask()
        validateSdk()
    }

    private fun configureCompileTask() {
        val compileTask = project.tasks.getByName("compileJava") as JavaCompile

        // We can't use lazy configuration here; we have to use project.afterEvaluate.
        project.afterEvaluate {
            val novelExt = project.extensions.getByType(NovelRTExtension::class.java)
            val originalMethod = novelExt.fixModules.getOrElse(FixModulesMethod.ANY)

            var method = originalMethod
            if (method == FixModulesMethod.ANY) {
                method = if (novelExt.moduleName.isPresent)
                    FixModulesMethod.PATCH_MODULE else
                    FixModulesMethod.COMBINE_COMPILED_CLASSES_FOLDERS
            }

            when (method) {
                FixModulesMethod.PATCH_MODULE -> {
                    KotlinModulesBuildConfigurator.configurePatchModules(compileTask)
                }

                FixModulesMethod.COMBINE_COMPILED_CLASSES_FOLDERS -> {
                    KotlinModulesBuildConfigurator.configureCombinedFolders(compileTask, originalMethod)
                }

                else -> project.logger.info("Skipped Kotlin module fix.")
            }
        }
    }

    private fun configureRunTask() {
        // We can't use lazy configuration here; we have to use project.afterEvaluate (again).
        project.afterEvaluate {
            try {
                val runTask = project.tasks.getByName("run") as JavaExec
                LibrarySearchPathConfigurator.configure(runTask)
                ResourcePathConfigurator.configure(runTask)
            } catch (e: UnknownTaskException) {
                project.logger.debug(
                    "No run task found (the application plugin is missing!), skipping library search path configuration."
                )
            }
        }
    }

    private fun createCombineResourcesTask() {
        val novelExt = project.extensions.getByType(NovelRTExtension::class.java)

        project.tasks.register("combineResources", Copy::class.java) {
            it.onlyIf {
                novelExt.runCombineResourcesTask.get()
            }

            if (novelExt.resourcesDir.isPresent) {
                it.from(novelExt.resourcesDir.get())
            }
            if (novelExt.sdk.get().engineResourcesDir.isPresent) {
                it.from(novelExt.sdk.get().engineResourcesDir.get())
            }

            it.into(novelExt.combinedResourcesDir.get())
        }

        project.tasks.named("assemble").configure {
            it.dependsOn("combineResources")
        }
    }

    private fun validateSdk() {
        project.afterEvaluate {
            val novelExt = project.extensions.getByType(NovelRTExtension::class.java)
            if (!novelExt.sdk.get().isComplete && novelExt.projectKind.get() == ProjectKind.NOVELRT_APPLICATION) {
                project.logger.error("The NovelRT SDK is incomplete for the {} application. " +
                        "Make sure to set all SDK properties in the NovelRT extension. " +
                        "You can disable this requirement by setting the projectKind to LIBRARY or OTHER, although " +
                        "this will disable some plugin features.",
                project.name)
            }
        }
    }
}

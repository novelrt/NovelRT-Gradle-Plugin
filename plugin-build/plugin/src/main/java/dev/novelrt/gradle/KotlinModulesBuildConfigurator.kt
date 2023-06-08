package dev.novelrt.gradle

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object KotlinModulesBuildConfigurator {
    fun configurePatchModules(task: JavaCompile) {
        val project = task.project
        val novelExt = project.extensions.getByType(NovelRTExtension::class.java)
        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)

        val mainModule = novelExt.moduleName.orNull
        if (mainModule != null) {
            // We need to explicitly specify where the compiled .class files are located.
            // Currently, the java compiler only uses its own build output directory, and
            // ignores Kotlin's directory; which makes Kotlin classes invisible and throws a bunch
            // of 'package not found' nonsense.
            //
            // The mainOutput variable contains a separated list of two directories: both Kotlin and Java
            // classes are there. So we can just use --patch-module to tell the Java compiler
            // that, yes, there actually ARE kotlin .class files in there and YES the package
            // you complain about actually exists, you blind fool.
            val mainOutput = sourceSets.getByName("main").output.asPath // javaClasses;kotlinClasses
            val patchModuleArg = "$mainModule=$mainOutput"

            task.options.compilerArgs.addAll(
                listOf(
                    "--patch-module", patchModuleArg
                )
            )

            project.logger.info("Added --patch-module argument in ${task.path} to fix Java 9 modules with Kotlin.")
            project.logger.debug("--patch-module argument: $patchModuleArg")
        } else {
            project.logger.warn(
                "Couldn't fix Java 9 modules for Kotlin; there's no moduleName set in the NovelRT plugin, or" +
                        "there's no mainModule set in the application plugin. " +
                        "This can cause issues with building the project."
            )
        }
    }

    fun configureCombinedFolders(compileTask: JavaCompile, originalMethod: FixModulesMethod) {
        val project = compileTask.project

        try {
            val kotlinTask = project.tasks.findByName("compileKotlin") as KotlinCompile
            kotlinTask.destinationDirectory.set(compileTask.destinationDirectory)

            var message =
                "Outputting Kotlin .class files in the Java output directory in order to fix Java 9 modules."
            if (originalMethod == FixModulesMethod.ANY) {
                message += " Specify moduleName in the NovelRT plugin to keep distinct folders and working modules."
            }
            project.logger.info(message)
        } catch (e: Exception) {
            project.logger.warn(
                "No compileKotlin task found while fixModule is set to COMBINE_COMPILED_CLASSES_FOLDERS", e
            )
        }
    }
}
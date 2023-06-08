package dev.novelrt.gradle

import org.gradle.api.file.Directory
import org.gradle.api.tasks.JavaExec
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

object LibrarySearchPathConfigurator {
    fun configure(task: JavaExec) {
        val project = task.project

        val novelExt = project.extensions.getByType(NovelRTExtension::class.java)
        val binaries = novelExt.sdk.get().binariesDir.orNull
        if (binaries != null) {
            configureLibrarySearchPath(binaries, task)
        } else {
            project.logger.info("Skipped adding NovelRT binaries to PATH")
        }
    }

    private fun configureLibrarySearchPath(binaries: Directory, runTask: JavaExec) {
        val project = runTask.project
        val os = DefaultNativePlatform.getCurrentOperatingSystem()
        val binariesPath = binaries.asFile.absolutePath
        when {
            os.isWindows -> {
                val currentPath = System.getenv("PATH")
                val pathWithLibs = "$currentPath;$binariesPath"
                runTask.environment["PATH"] = pathWithLibs

                project.logger.info("Appended NovelRT binaries ($binariesPath) to PATH")
            }

            os.isLinux -> {
                val currentPath = System.getenv("LD_LIBRARY_PATH")
                val pathWithLibs = "$currentPath:$binariesPath"
                runTask.environment["LD_LIBRARY_PATH"] = pathWithLibs

                project.logger.info("Appended NovelRT binaries ($binariesPath) to LD_LIBRARY_PATH")
            }

            os.isMacOsX -> {
                val currentPath = System.getenv("DYLD_LIBRARY_PATH")
                val pathWithLibs = "$currentPath:$binariesPath"
                runTask.environment["DYLD_LIBRARY_PATH"] = pathWithLibs

                project.logger.info("Appended NovelRT binaries ($binariesPath) to DYLD_LIBRARY_PATH")
            }
        }
    }
}
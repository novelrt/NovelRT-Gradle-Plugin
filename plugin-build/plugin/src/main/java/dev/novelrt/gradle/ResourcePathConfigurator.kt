package dev.novelrt.gradle

import org.gradle.api.tasks.JavaExec

object ResourcePathConfigurator {
    fun configure(task: JavaExec) {
        val novelExt = task.project.extensions.getByType(NovelRTExtension::class.java)

        val resourcesPath = novelExt.combinedResourcesDir.orNull
        if (resourcesPath != null) {
            task.systemProperties["novelrt.resources.path"] = resourcesPath

            task.project.logger.info("novelrt.resources.path has been set to $resourcesPath")
        }
    }
}

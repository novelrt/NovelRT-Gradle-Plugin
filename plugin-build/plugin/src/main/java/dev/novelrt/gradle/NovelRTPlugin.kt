package dev.novelrt.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.platform.base.ApplicationSpec


abstract class NovelRTPlugin : Plugin<Project> {
    companion object {
        const val EXTENSION_NAME = "novelrt"
    }

    override fun apply(project: Project) {
        project.plugins.apply(JavaPlugin::class.java)
        project.plugins.apply(ApplicationPlugin::class.java)

        project.extensions.create(EXTENSION_NAME, NovelRTExtension::class.java, project)

        configureJavaModular(project)
        createConfigureModuleBuildTask(project)
    }

    private fun createConfigureModuleBuildTask(project: Project) {
        project.tasks.create("configureModuleBuild", ConfigureModuleBuildTask::class.java)
        project.tasks.getByName("compileJava").dependsOn("configureModuleBuild")
    }

    private fun configureJavaModular(project: Project) {
        val javaExtension = project.extensions.getByType(JavaPluginExtension::class.java)
        javaExtension.modularity.inferModulePath.set(true)
    }
}

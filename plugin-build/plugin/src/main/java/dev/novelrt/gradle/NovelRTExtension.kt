// Copyright © Matt Jones and Contributors. Licensed under the MIT Licence (MIT). See LICENCE.md in the repository root
// for more information.

package dev.novelrt.gradle

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class NovelRTExtension @Inject constructor(project: Project) {
    private val objects = project.objects

    val projectKind: Property<ProjectKind> = objects.property(ProjectKind::class.java).convention(project.provider {
        when (project.plugins.hasPlugin(ApplicationPlugin::class.java)) {
            true -> ProjectKind.NOVELRT_APPLICATION
            false -> ProjectKind.LIBRARY
        }
    })

    val combinedResourcesDir: DirectoryProperty = objects.directoryProperty().convention(
        project.layout.buildDirectory.dir("combinedResources")
    )

    val runCombineResourcesTask: Property<Boolean> = objects.property(Boolean::class.java).convention(projectKind.map {
        it == ProjectKind.NOVELRT_APPLICATION
    })

    val resourcesDir: DirectoryProperty = objects.directoryProperty().convention(
        project.layout.projectDirectory.dir("resources")
    )

    val moduleName: Property<String?> = objects.property(String::class.java).convention(project.provider {
        project.extensions.findByType(JavaApplication::class.java)?.mainModule?.orNull
    })

    val fixModules: Property<FixModulesMethod> =
        objects.property(FixModulesMethod::class.java).convention(
            when (project.plugins.hasPlugin("org.jetbrains.kotlin.jvm")) {
                true -> FixModulesMethod.ANY
                false -> FixModulesMethod.DISABLED
            }
        );

    val sdk: Property<NovelRTSdk> = objects.property(NovelRTSdk::class.java).convention(NovelRTSdk(objects))
    fun sdk(action: NovelRTSdk.() -> Unit) {
        action(sdk.get())
    }
}


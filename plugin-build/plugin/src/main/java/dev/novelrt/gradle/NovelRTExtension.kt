// Copyright © Matt Jones and Contributors. Licensed under the MIT Licence (MIT). See LICENCE.md in the repository root
// for more information.

package dev.novelrt.gradle

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.provider.Property
import org.gradle.api.tasks.SourceSetContainer
import javax.inject.Inject

abstract class NovelRTExtension @Inject constructor(project: Project) {
    private val objects = project.objects

    val binariesLocation: DirectoryProperty = objects.directoryProperty()
    val resourcesLocation: DirectoryProperty = objects.directoryProperty().convention(
        project.extensions.getByType(SourceSetContainer::class.java).named("main")
            .map { it.resources.sourceDirectories.first() }
            .let(project.layout::dir)
    )

    val mainModule: Property<String?> = objects.property(String::class.java).convention(
        project.extensions.findByType(JavaApplication::class.java)?.mainModule ?: project.provider { null }
    )
}

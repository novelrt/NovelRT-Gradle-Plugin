package dev.novelrt.gradle

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory

class NovelRTSdk(objects: ObjectFactory) {
    val binariesDir: DirectoryProperty = objects.directoryProperty()
    val engineResourcesDir: DirectoryProperty = objects.directoryProperty()

    val isComplete: Boolean get() = binariesDir.isPresent && engineResourcesDir.isPresent
}
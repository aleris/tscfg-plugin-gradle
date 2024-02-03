package io.github.aleris.plugins.tscfg

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class TscfgExtension @Inject constructor(objects: ObjectFactory, project: Project) {
  val files: NamedDomainObjectContainer<ConfigFile> = objects.domainObjectContainer(ConfigFile::class.java) { name ->
    objects.newInstance(ConfigFile::class.java, name, packageName, objects)
  }
  val packageName: Property<String> = objects.property(String::class.java).convention(
    packageNameFromGroup(
      project.group.toString()
    )
  )
  val generateConfigFile: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
  val configFileIndent: Property<String> = objects.property(String::class.java).convention("  ")
  val outputInGeneratedResourceSet: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
  val addGeneratedAnnotation: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
  val generateGetters: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
  val generateRecords: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
  val useOptionals: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
  val useDurations: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

  companion object {
    private fun packageNameFromGroup(group: String) = group.replace("[^a-zA-Z-]+", ".").lowercase()
  }
}

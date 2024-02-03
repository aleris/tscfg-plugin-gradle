package io.github.aleris.plugins.tscfg

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

open class ConfigFile @Inject constructor(val name: String, globalPackageName: Property<String>, objects: ObjectFactory) {
  @get:Input
  val packageName: Property<String> = objects.property(String::class.java).convention(globalPackageName.getOrElse(""))

  @get:Input
  val outputConfigFileName: Property<String> = objects.property(String::class.java).convention(configFileName(name))

  @get:Input
  val className: Property<String> = objects.property(String::class.java).convention(classNameFromConfigFileName(name))

  companion object {
    private fun configFileName(name: String) = name.replace(".spec.conf", ".conf")
    private fun classNameFromConfigFileName(name: String) = name
      .substringAfterLast("/")
      .substringBefore(".")
      .replace(Regex("^[0-9]+"), "_")
      .replace(Regex("[^a-zA-Z0-9_]+"), "")
      .replaceFirstChar { it.uppercase() } + "Config"
  }
}

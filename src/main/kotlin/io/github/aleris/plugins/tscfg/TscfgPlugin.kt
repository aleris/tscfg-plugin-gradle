package io.github.aleris.plugins.tscfg

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

/**
 * Plugin that wraps the tscfg code generator for typesafe configuration files and associated POJO like classes.
 */
class TscfgPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val extension = project.extensions.create("tscfg", TscfgExtension::class.java)

    project.tasks.register("generateTscfg", GenerateTscfgTask::class.java) { task ->
      task.group = "build"
      task.description = "Generates typesafe configurations and java classes from specification files using tscfg"

      if (extension.files.isNotEmpty()) {
        task.files.set(extension.files)
      } else {
        val defaultConfigFile = project.objects.newInstance(ConfigFile::class.java, "application", extension, project)
        task.files.add(defaultConfigFile)
      }
      task.generateConfigFile.set(extension.generateConfigFile)
      task.configFileIndent.set(extension.configFileIndent)
      task.outputInGeneratedResourceSet.set(extension.outputInGeneratedResourceSet)
      task.addGeneratedAnnotation.set(extension.addGeneratedAnnotation)
      task.generateGetters.set(extension.generateGetters)
      task.generateRecords.set(extension.generateRecords)
      task.useOptionals.set(extension.useOptionals)
      task.useDurations.set(extension.useDurations)

      if (extension.outputInGeneratedResourceSet.get()) {
        val javaPluginAvailable = project.plugins.findPlugin(JavaPlugin::class.java) != null
        if (javaPluginAvailable) {
          val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
          val main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
          main.java.srcDir(project.layout.buildDirectory.dir(GENERATED_SOURCES_ROOT))
        } else {
          project.logger.warn("Java plugin not applied. tscfg plugin cannot register generated source set.")
        }
      }
    }
  }

  companion object {
    const val GENERATED_SOURCES_ROOT = "generated/sources/io/github/aleris/tscfg"
  }
}

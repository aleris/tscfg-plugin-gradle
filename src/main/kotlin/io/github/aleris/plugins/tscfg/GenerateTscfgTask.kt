package io.github.aleris.plugins.tscfg

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class GenerateTscfgTask : DefaultTask() {
  @get:Input
  abstract val files: ListProperty<ConfigFile>

  @get:Input
  abstract val generateConfigFile: Property<Boolean>

  @get:Input
  abstract val configFileIndent: Property<String>

  @get:Input
  abstract val outputInGeneratedResourceSet: Property<Boolean>

  @get:Input
  abstract val addGeneratedAnnotation: Property<Boolean>

  @get:Input
  abstract val generateGetters: Property<Boolean>

  @get:Input
  abstract val generateRecords: Property<Boolean>

  @get:Input
  abstract val useOptionals: Property<Boolean>

  @get:Input
  abstract val useDurations: Property<Boolean>

  @TaskAction
  fun generate() {
    val sourceConfigFileReader = SourceConfigFileReader(project)
    val configGenerator = ConfigGenerator(this)
    val configWriter = ConfigWriter(configGenerator)
    val classGenerator = ClassGenerator(this)
    val classWriter = ClassWriter(classGenerator)
    val generateConfigFile = generateConfigFile.get()

    files.get().forEach { configFile ->
      val sourceConfig = sourceConfigFileReader.read(configFile)

      println("Generating tscfg from spec ${sourceConfig.path}")

      classWriter.write(sourceConfig)

      if (generateConfigFile) {
        configWriter.write(sourceConfig)
      }
    }
  }
}

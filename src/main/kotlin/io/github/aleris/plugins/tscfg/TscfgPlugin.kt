package io.github.aleris.plugins.tscfg

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

class TscfgPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val extension = project.extensions.create("tscfg", TscfgExtension::class.java)

    project.tasks.register("generateTscfg", GenerateTscfgTask::class.java) { task ->
      task.group = "build"
      task.description = "Generates typesafe configurations and accompanied tsfcg classes from specification files"

      task.files.set(extension.files)
      task.generateConfigFile.set(extension.generateConfigFile)
      task.configFileIndent.set(extension.configFileIndent)
      task.outputInGeneratedResourceSet.set(extension.outputInGeneratedResourceSet)
      task.addGeneratedAnnotation.set(extension.addGeneratedAnnotation)
      task.generateGetters.set(extension.generateGetters)
      task.generateRecords.set(extension.generateRecords)
      task.useOptionals.set(extension.useOptionals)
      task.useDurations.set(extension.useDurations)
    }

    project.plugins.withType(JavaPlugin::class.java) {
      val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
      val main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
      main.java.srcDir(project.layout.buildDirectory.dir(GENERATED_SOURCES_ROOT))

      project.tasks.named("compileJava").configure {
        it.dependsOn("generateTscfg")
      }
    }
  }

  companion object {
    const val GENERATED_SOURCES_ROOT = "generated/sources/io/github/aleris/tscfg"
  }
}

package io.github.aleris.plugins.tscfg

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension

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

    project.plugins.apply(JavaPlugin::class.java)

    // project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
    //            SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
    //            SourceSet main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    //            main.getJava().setSrcDirs(Arrays.asList("src"));
    //        });

    // project.getTasks().withType(War.class).configureEach(war ->
    //            war.setWebXml(project.file("src/someWeb.xml")));

    val compileJavaTask = project.tasks.named("compileJava")
    compileJavaTask.configure {
      it.dependsOn("generateTscfg")
    }

    val javaPluginExtension = project.extensions.findByType(JavaPluginExtension::class.java)
      ?: throw GradleException("JavaPluginExtension not found")

    val sourceDirectorySet = javaPluginExtension.sourceSets.findByName("main")?.java
      ?: throw GradleException("Source set main/java not found")

    sourceDirectorySet.srcDir(project.layout.buildDirectory.dir(GENERATED_SOURCES_ROOT))
  }

  companion object {
    const val GENERATED_SOURCES_ROOT = "generated/sources/io/github/aleris/tscfg"
  }
}

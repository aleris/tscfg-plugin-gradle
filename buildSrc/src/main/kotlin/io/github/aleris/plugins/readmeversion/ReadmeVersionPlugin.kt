package io.github.aleris.plugins.readmeversion

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

class ReadmeVersionPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.tasks.register("updateReadmeVersion", ReadmeVersionTask::class.java) {
      group = "build"
      description = "Keep the version in the README.md file up to date with the project version"
    }

    project.tasks.named("build").configure {
      dependsOn("updateReadmeVersion")
    }
  }
}

open class ReadmeVersionTask : DefaultTask() {
  @TaskAction
  fun updateReadmeVersion() {
    val readmeFile = project.file("README.md")
    if (!readmeFile.exists()) {
      project.logger.warn("No README.md file found")
      return
    }
    val readmeContent = readmeFile.readText()
    val newReadmeContent = readmeContent
      .replace(Regex("\"\\d+\\.\\d+\\.\\d+\""), "\"${project.version}\"")
      .replace(Regex("'\\d+\\.\\d+\\.\\d+'"), "'${project.version}'")
    if (readmeContent != newReadmeContent) {
      project.logger.lifecycle("Updating README.md with new version ${project.version}")
      readmeFile.writeText(newReadmeContent)
    }
  }
}

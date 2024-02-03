package io.github.aleris.plugins.tscfg

import org.gradle.api.Project
import tscfg.ModelBuilder
import tscfg.ns.NamespaceMan

data class SourceConfigFileReader(private val project: Project) {
  fun read(file: ConfigFile): ConfigSource {
    // src/main/resources/${file.specFileName.get()}
    val path = project.file(file.name).toPath()
    val source = path.toFile().readText()
    val namespace = NamespaceMan()
    val objectType = ModelBuilder.apply(namespace, source, false).objectType()
    return ConfigSource(file, path, objectType, namespace)
  }
}

package io.github.aleris.plugins.tscfg

class ClassWriter(private val classGenerator: ClassGenerator) {
  private val task: GenerateTscfgTask = classGenerator.task

  fun write(configSource: ConfigSource) {
    val project = task.project

    val configFile = configSource.configFile

    val generatedSourcesRoot = TscfgPlugin.GENERATED_SOURCES_ROOT
    val packageName = configFile.packageName.get()
    val packagePath = packagePath(packageName)
    val outputInGeneratedResourceSet = task.outputInGeneratedResourceSet.get()
    val outDir = if (outputInGeneratedResourceSet) {
      project.layout.buildDirectory
        .file("$generatedSourcesRoot/java/$packagePath")
        .get()
        .asFile
    } else {
      project.file("src/main/java/$packagePath")
    }

    val className = configFile.className.get()
    val outPath = outDir.toPath().resolve("$className.java")

    project.logger.info("Writing config class ${configFile.className} to $outPath")
    outDir.mkdirs()

    val code = classGenerator.generate(configSource)

    UnchangedFileWriter.write(outPath, code)
  }

  private fun packagePath(packageName: String) = packageName.replace(".", "/")
}

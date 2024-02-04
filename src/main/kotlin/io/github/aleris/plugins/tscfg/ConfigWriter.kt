package io.github.aleris.plugins.tscfg

class ConfigWriter(private val configGenerator: ConfigGenerator) {
  private val task = configGenerator.task

  fun write(configSource: ConfigSource) {
    val configFile = configSource.configFile

    val project = task.project
    val outFile = configFile.configFile.get().asFile
    outFile.parentFile.mkdirs()
    val outPath = outFile.toPath()
    project.logger.info("Writing config file to $outPath")

    val source = configGenerator.generate(configSource)
    UnchangedFileWriter.write(outPath, source)
  }
}
